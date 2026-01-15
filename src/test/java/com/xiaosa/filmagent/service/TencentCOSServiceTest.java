package com.xiaosa.filmagent.service;

import com.xiaosa.filmagent.component.TencentCOSService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TencentCOSServiceTest {
    @Resource
    private TencentCOSService tencentCOSService;
    @Test
    void putObject() {
    }

    @Test
    void getObjectTemporaryUrl() {
        String objectTemporaryUrl = tencentCOSService.getObjectTemporaryUrl("xxxhaha.png");
        System.out.println(objectTemporaryUrl);
    }

    @Test
    void deleteObject() {
    }

    @Test
    void objectExists() {
    }
}