package com.root.mailservice.services;

import com.root.mailservice.entities.EmailEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailService {
    @Value("${spring.mail.username}")
    private static String FROM;
    
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMailMessage(EmailEntity email) {
        if (email == null) {
            throw new RuntimeException("Email can't be null");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(email.getTo());
        message.setSubject(email.getSubject());
        message.setText(email.getMessage());

        this.mailSender.send(message);
    }
}
