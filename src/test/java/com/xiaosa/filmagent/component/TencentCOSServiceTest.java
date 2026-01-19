package com.xiaosa.filmagent.component;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TencentCOSServiceTest {
    @Resource
    private TencentCOSService tencentCOSService;

    @Test
    void putObject() {
//        System.out.println(tencentCOSService.putObject("D:/IntellijIDEA/project/ai/FilmAgent/tmp/document/Action Movies.md"));
    }

    @Test
    void getObjectTemporaryUrl() {
        System.out.println(tencentCOSService.getObjectTemporaryUrl("md/Action Movies.md"));
    }

    @Test
    void downloadMarkdownAsResource() {
        System.out.println(tencentCOSService.downloadMarkdownAsResource("md/Action Movies.md"));
    }

    @Test
    void getObjectMetadata() {
        String objectMetadata = tencentCOSService.getObjectMetadata("md/Science Fiction Movies.md");
        System.out.println(objectMetadata);
    }

    @Test
    void getObjectTags() {
        String objectTags = tencentCOSService.getObjectTags("md/Animated Movies.md");
        System.out.println(objectTags);
    }
}