package com.xiaosa.filmagent.aitools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourcesDownloadToolTest {

    @Test
    void download() {
        ResourcesDownloadTool resourcesDownloadTool = new ResourcesDownloadTool();
        String s = resourcesDownloadTool.download("https://picx.zhimg.com/80/v2-4883cb9d7a8084f84aa0bae861694cd1_720w.webp?source=2c26e567",
                "杰西卡.png");
    }
}