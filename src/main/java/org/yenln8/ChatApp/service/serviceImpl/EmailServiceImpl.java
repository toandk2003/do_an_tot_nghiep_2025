package org.yenln8.ChatApp.service.serviceImpl;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.SendEmailResponseDto;
import org.yenln8.ChatApp.service.EmailService;

import java.util.Properties;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {
    private static Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

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
                logger.info("OTP sent successfully to: " + recipientEmail);

                return SendEmailResponseDto.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Email sent successfully to: " + recipientEmail)
                        .build();

            } catch (MessagingException e) {
                logger.error("Failed to send OTP: " + e.getMessage());
                return  SendEmailResponseDto.builder()
                        .success(false)
                        .statusCode(200)
                        .message("Email sent successfully to: " + recipientEmail)
                        .build();
            }

    }
    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6 digit OTP
        return String.valueOf(otp);
    }

}
