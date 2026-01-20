package com.xiaosa.filmagent.service.impl;

import com.xiaosa.filmagent.component.TencentCOSService;
import com.xiaosa.filmagent.rag.FilmKeywordEnricher;
import com.xiaosa.filmagent.rag.FilmMarkdownReader;
import com.xiaosa.filmagent.service.RagService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.QueryResults;
import io.milvus.param.R;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 代码介绍
 * 该类是 rag 服务的实现类，主要完成rag服务，包括上传文件、删除文件
 * 两个部分 1.COS 对象存储 2.Milvus向量存储
 * 1 COS对象存储使用腾讯云对象存储
 * 通过前端页面和后端controller接口，前端文件上传到cos对象存储中
 * 后端代码保存了自定义元数据，格式为 file-id: uuid
 * 也自定义了标签，格式为 文档切割后的dos_id以“,”拼接成的字符串
 * 2 Milvus向量存储
 * 自定义了元数据：file_id: uuid, film_name: 文件名
 *
 * todo 根据实际需求，应该使用关系型数据库保存 cos对象和milvus向
 *      量的关系。
 *      需要存储的
 *      - cos 文档的key
 *      - cos 文档的文件名
 *      - cos 文档的uuid
 *      - cos拓展信息 上传者，时间，等
 *      - milvus向量存储的向量id：经过文档切割后往往有较多的向量
 *      - 其他：文件拓展信息：文件类型，等。按照实际需求
 * todo 1. 异常处理
 *      2. 删除功能的一致性
 */

@Service
public class RagServiceImpl implements RagService {
    private static final Logger log = LoggerFactory.getLogger(RagServiceImpl.class);

    private final TencentCOSService tencentCOSService;
    private final FilmMarkdownReader filmMarkdownReader;
    private final MilvusVectorStore vectorStore;
    private final FilmKeywordEnricher filmKeywordEnricher;
    private final MilvusServiceClient filmMilvusServiceClient;
    public RagServiceImpl(TencentCOSService tencentCOSService
                        , FilmMarkdownReader filmMarkdownReader
                        , MilvusVectorStore vectorStore
                        , FilmKeywordEnricher filmKeywordEnricher
                        , MilvusServiceClient filmMilvusServiceClient) {
        this.tencentCOSService = tencentCOSService;
        this.filmMarkdownReader = filmMarkdownReader;
        this.vectorStore = vectorStore;
        this.filmKeywordEnricher = filmKeywordEnricher;
        this.filmMilvusServiceClient = filmMilvusServiceClient;
    }

    /**
     * 代码介绍
     * 上传cos对象存储时，设置了一个uuid作为自定义的元数据，
     * 并将分割后的document作为标签，上传至对象存储。
     * 对象存储的键称格式为：拓展名/文件名 如 md/films.md
     * todo 对象存储的标签使用不合理
     */
    @Override
    public boolean ingestDocument(MultipartFile file) {
        // 1. 安全获取文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("上传的文件名为空");
        }
        // 2. 构造 COS key（建议加 UUID 避免冲突）
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String cosKey = extension + "/" + originalFilename; // 或更安全：extension + "/" + UUID + "." + extension
        // 3. 准备元数据
        String fileId = UUID.randomUUID().toString();
        Map<String, Object> metadata = Map.of(
                "file_id", fileId,
                "film_name", originalFilename
        );
        try {
            // String content = new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            List<Document> documents = filmMarkdownReader.loadMarkdown(file, metadata);
            // 5. 向量化 & 存储
            documents = filmKeywordEnricher.enrichDocuments(documents);
            vectorStore.add(documents);
            List<String> vectorStore_ids = documents.stream().map(Document::getId).toList();
            // 6. 上传到 COS：用 InputStream 而不是 File
            tencentCOSService.putObject(file.getInputStream(), cosKey, fileId ,vectorStore_ids);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("处理文件 [" + originalFilename + "] 时出错", e);
        }
    }

    /**
     * 删除功能讲解
     * 前提：cos对象存储的键的格式未  拓展名/文件名 如 md/films.md
     * 1. 删除COS文件
     *  较为简单，略
     * 2. 删除向量
     *  使用MilvusServiceClient客户端和指定表达式删除。
     *  删除的字段 为 metadata["film_name"]，且metadata为json格式
     *  milvus向量存储是，metadata下有film_name,file_id都可以作为删除条件
     */
    @Override
    public boolean deleteDocument(String key) {
        // 删除cos文件
        tencentCOSService.deleteObject(key);
        // 删除向量
        try {
            DeleteParam deleteParam = DeleteParam.newBuilder()
                    .withCollectionName("vector_store_film")
                    .withExpr("metadata[\"film_name\"] == \"" + key.substring(key.lastIndexOf("/")+1) + "\"")
                    .build();
            R<MutationResult> delete = filmMilvusServiceClient.delete(deleteParam);
            if(delete.getStatus() != R.success().getStatus()){
                log.error("删除向量失败", delete.getMessage());
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void query(){
        QueryParam queryParam = QueryParam.newBuilder()
                .withCollectionName("vector_store_film")
                .withExpr("metadata[\"file_name\"] == \"Science Fiction Movies.md\"")
                .withLimit(10L)
                .build();

        R<QueryResults> queryResp =  filmMilvusServiceClient.query(queryParam);
        System.out.println("Matched count: " + queryResp.getData());
    }

    @Override
    public boolean existDocument(String key) {
        return tencentCOSService.objectExists(key);
    }
}
