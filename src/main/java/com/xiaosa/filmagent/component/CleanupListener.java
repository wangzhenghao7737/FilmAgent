package com.xiaosa.filmagent.component;

import com.qcloud.cos.COSClient;
import jakarta.annotation.Resource;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//@Component
public class CleanupListener {
//    @Resource
    private COSClient cosClient;
//    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        cosClient.shutdown();
    }
}