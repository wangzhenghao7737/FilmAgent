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

import java.util.HashMap;
import java.util.List;
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
    public boolean ingestDocument(String filePath) {
        // 1 参数校验
        if(!StringUtils.hasText(filePath)){
            return false;
        }
        // 2 文件类型校验
        if(!filePath.endsWith(".md")){
            return false;
        }
        // 3 cos文件上传
        String key = tencentCOSService.putObject(filePath);
        // 4 文档收集与切割
        HashMap<String, Object> metadata = new HashMap<>();
        final String uuid = UUID.randomUUID().toString();

        String type = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
        // todo 添加元数据，用于检索和溯源
        metadata.put("file_id",uuid);
        metadata.put("film_type", type);
        metadata.put("film_name", filePath.substring(filePath.lastIndexOf("/") + 1));
        Resource resource = tencentCOSService.downloadMarkdownAsResource(key);
        List<Document> documents = filmMarkdownReader.loadMarkdown(resource,metadata);
        // 5 向量存储与转换（携带自定义元数据）
        documents = filmKeywordEnricher.enrichDocuments(documents);
        vectorStore.add(documents);
//         6 更新cos元数据，使包含切割后的数据id
//        List<String> ids = documents.stream().map(Document::getId).toList();
//        tencentCOSService.updateVectorIdsMetadata(key, ids);
        return true;
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
            vectorStore.delete("metadata[\"film_name\"] == \"" + key + "\"");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
