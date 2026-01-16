package com.xiaosa.filmagent.service.impl;

import com.xiaosa.filmagent.component.TencentCOSService;
import com.xiaosa.filmagent.rag.CustomVectorStore;
import com.xiaosa.filmagent.rag.FilmKeywordEnricher;
import com.xiaosa.filmagent.rag.FilmMarkdownReader;
import com.xiaosa.filmagent.service.RagService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

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
        String type = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
        metadata.put("film_type", type);
        Resource resource = tencentCOSService.downloadMarkdownAsResource(key);
        List<Document> documents = filmMarkdownReader.loadMarkdown(resource,metadata);
        // 5 向量存储与转换（携带自定义元数据）
        documents = filmKeywordEnricher.enrichDocuments(documents);
        vectorStore.add(documents);
        return false;
    }
}
