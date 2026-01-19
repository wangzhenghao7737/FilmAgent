package com.xiaosa.filmagent.service.impl;

import com.xiaosa.filmagent.component.TencentCOSService;
import com.xiaosa.filmagent.rag.FilmKeywordEnricher;
import com.xiaosa.filmagent.rag.FilmMarkdownReader;
import com.xiaosa.filmagent.service.RagService;
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

@Service
public class RagServiceImpl implements RagService {
    private final TencentCOSService tencentCOSService;
    private final FilmMarkdownReader filmMarkdownReader;
    private final MilvusVectorStore vectorStore;
    private final FilmKeywordEnricher filmKeywordEnricher;
    public RagServiceImpl(TencentCOSService tencentCOSService
                        , FilmMarkdownReader filmMarkdownReader
                        , MilvusVectorStore vectorStore
                        , FilmKeywordEnricher filmKeywordEnricher) {
        this.tencentCOSService = tencentCOSService;
        this.filmMarkdownReader = filmMarkdownReader;
        this.vectorStore = vectorStore;
        this.filmKeywordEnricher = filmKeywordEnricher;

    }
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



    @Override
    public boolean deleteDocument(String key) {
        // 删除cos文件
        // 删除向量
        try {
//            Filter.Expression expression = new Filter.Expression(
//                    Filter.ExpressionType.EQ,
//                    new Filter.Key("metadata.film_type"),           // 左操作数为字段名
//                    new Filter.Value(key)               // 右操作数为值
//            );
            vectorStore.delete("metadata['film_name'] == '" + key + "'");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
