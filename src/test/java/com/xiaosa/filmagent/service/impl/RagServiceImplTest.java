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
    void testDeleteDocument() {
        boolean b = ragService.deleteDocument("md/Science Fiction Movies.md");
    }

    @Test
    void query() {
        ragService.query();
    }
}