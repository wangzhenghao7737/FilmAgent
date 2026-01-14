package com.xiaosa.filmagent.aitools;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 向用户发送邮件
 * todo 1.获取当前用户的邮箱地址，用redis,mysql或ThreadLocal.2.拓展邮箱功能，如支持附件，链接等
 */
@Component
public class EmailServiceTool {

    private final JavaMailSender javaMailSender;
    private final String from;
    public EmailServiceTool(@Value("${spring.mail.username}") String from
    ,JavaMailSender javaMailSender) {
        this.from = from;
        this.javaMailSender = javaMailSender;
    }

    @Tool(description = "Send an email")
    public String sendEmail(@ToolParam(description = "Subject of the email") String subject
            ,@ToolParam(description = "The content of the email (must not be empty)") String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        // todo 使用 ThreadLocal，Redis缓存，或查询数据库等方式获取用户邮箱。当前先发送给自己
        message.setTo(from);
        message.setSubject(subject);
        message.setText(text);
        try {
            javaMailSender.send(message);
            return "Email sent successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending email: Try again later";
        }
    }
}
