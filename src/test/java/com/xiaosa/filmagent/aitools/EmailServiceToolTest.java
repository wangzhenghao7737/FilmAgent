package com.xiaosa.filmagent.aitools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceToolTest {
    @Resource
    private EmailServiceTool emailServiceTool;

    @Test
    void sendEmail() {
        String s = emailServiceTool.sendEmail("测试邮件", "测试邮件内容");
        System.out.println(s);
    }
}