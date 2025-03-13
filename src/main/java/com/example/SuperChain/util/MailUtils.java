package com.example.SuperChain.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author shizhishi
 */
@Component
@Slf4j
public class MailUtils {

    private final JavaMailSender javaMailSender;  //注入JavaMailSender实例

    @Value("${spring.mail.username}")
    private String from;

    public MailUtils(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * 发送HTML格式的邮件
     *
     * @param to      邮件接收者
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Async
     public void sendHtmlMail(String to, String subject, String content) {
        try {
            MimeMessage message = this.javaMailSender.createMimeMessage();  // 创建MimeMessage对象
            MimeMessageHelper helper = new MimeMessageHelper(message, true);  // 创建MimeMessageHelper对象
            helper.setFrom(from);  // 设置邮件发送者
            helper.setTo(to);  // 设置邮件接收者
            helper.setSubject(subject);  // 设置邮件主题
            helper.setText(content, true);  // 设置邮件内容为HTML格式
            this.javaMailSender.send(message);  // 发送邮件
        } catch (MessagingException e) {
            log.error("发送邮件失败！", e);
        }
    }
}
