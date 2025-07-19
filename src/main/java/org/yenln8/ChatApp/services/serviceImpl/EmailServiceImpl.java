package org.yenln8.ChatApp.services.serviceImpl;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;
import org.yenln8.ChatApp.services.EmailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {
    private static Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private  static final Map<String, List<Long>> DEVICE_SEND_MAIL_HISTORY = new HashMap<>();

    @Value("${email.sender.address}")
    private String systemEmailSender;

    @Value("${email.sender.password}")
    private String senderPassword;

    @Override
    public SendEmailResponseDto systemSendTo(String recipientEmail, String subject, String content) {
            try {
                // Setup mail properties
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                // Create session
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(systemEmailSender, senderPassword);
                    }
                });

                // Create message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(systemEmailSender));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject(subject);
                message.setContent(content, "text/html; charset=utf-8");

                // Send
                Transport.send(message);
                logger.info("OTP sent successfully to: {}", recipientEmail);

                return SendEmailResponseDto.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Email sent successfully to: " + recipientEmail)
                        .build();

            } catch (MessagingException e) {
                logger.error("Failed to send OTP: {}", e.getMessage());
                return  SendEmailResponseDto.builder()
                        .success(false)
                        .statusCode(500)
                        .message("Email sent fail to: " + recipientEmail)
                        .build();
            }

    }
}
