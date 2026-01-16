package com.xiaosa.filmagent.rag;

import com.xiaosa.filmagent.component.TencentCOSService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmMarkdownReaderTest {
    @Resource
    private FilmMarkdownReader filmMarkdownReader;
    @Resource
    private TencentCOSService tencentCOSService;
    @Test
    void loadMarkdown() {
        org.springframework.core.io.Resource resource = tencentCOSService.downloadMarkdownAsResource("md/Action Movies.md");
        List<Document> documents = filmMarkdownReader.loadMarkdown(resource,null);
        System.out.println( documents);
    }
}