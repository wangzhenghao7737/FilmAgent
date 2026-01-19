package com.xiaosa.filmagent.service.impl;

import com.xiaosa.filmagent.service.RagService;
import io.milvus.client.MilvusClient;
import io.milvus.v2.service.vector.request.DeleteReq;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RagServiceImplTest {
    @Resource
    private RagService ragService;
    @Autowired
    private MilvusClient milvusClient;
    @Test
    void ingestDocument() {
//        ragService.ingestDocument("D:/IntellijIDEA/project/ai/FilmAgent/tmp/document/Science Fiction Movies.md");
    }

    @Test
    void deleteDocument() {
        String expr = "metadata['film_name'] == 'Science Fiction Movies.md'";

        DeleteReq deleteReq = DeleteReq.builder()
                .collectionName("metadata")
                .filter(expr)
                .build();
//        boolean b = ragService.deleteDocument("Science Fiction Movies.md");
//        milvusClient.delete(deleteReq);
//        System.out.println(b);
    }

}