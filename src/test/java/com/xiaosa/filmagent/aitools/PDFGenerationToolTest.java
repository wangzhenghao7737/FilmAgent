package com.xiaosa.filmagent.aitools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PDFGenerationToolTest {

    @Resource
    private PDFGenerationTool pdfGenerationTool;
    @Test
    void generatePDF() {
        String result = pdfGenerationTool.generatePDF("test.pdf", "Hello, World!");
        System.out.println(result);
    }
}