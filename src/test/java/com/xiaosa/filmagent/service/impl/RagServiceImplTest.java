package com.xiaosa.filmagent.service.impl;

import com.xiaosa.filmagent.service.RagService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RagServiceImplTest {
    @Resource
    private RagService ragService;
    @Test
    void ingestDocument() {
        ragService.ingestDocument("D:/IntellijIDEA/project/ai/FilmAgent/tmp/document/Science Fiction Movies.md");
    }
}