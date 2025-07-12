//package org.yenln8.ChatApp.service;
//import jakarta.mail.*;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeMessage;
//import java.util.Properties;
//import java.util.Random;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class GmailOTPSender {
//
//    private static final String SMTP_HOST = "smtp.gmail.com";
//    private static final String SMTP_PORT = "587";
//    private String senderEmail;
//    private String senderPassword; // App Password từ Google
//
//    public GmailOTPSender(String senderEmail, String senderPassword) {
//        this.senderEmail = senderEmail;
//        this.senderPassword = senderPassword;
//    }
//
//    // Generate OTP 6 số
//
//    // Gửi OTP
//    public boolean sendOTP(String recipientEmail, String otp) {
//        try {
//            // Setup mail properties
//            Properties props = new Properties();
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.host", SMTP_HOST);
//            props.put("mail.smtp.port", SMTP_PORT);
//            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
//
//            // Create session
//            Session session = Session.getInstance(props, new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(senderEmail, senderPassword);
//                }
//            });
//
//            // Create message
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(senderEmail));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
//            message.setSubject("Your OTP Code");
//
//            // Email content
//            String emailContent = createOTPEmailContent(otp);
//            message.setContent(emailContent, "text/html; charset=utf-8");
//
//            // Send
//            Transport.send(message);
//            System.out.println("OTP sent successfully to: " + recipientEmail);
//            return true;
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send OTP: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    // Tạo nội dung email HTML
//    private String createOTPEmailContent(String otp) {
//        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
//
//        return String.format("""
//            <html>
//            <body style="font-family: Arial, sans-serif;">
//                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
//                    <h2 style="color: #333;">Mã xác thực OTP</h2>
//                    <p>Xin chào,</p>
//                    <p>Mã OTP của bạn là:</p>
//                    <div style="background-color: #f4f4f4; padding: 20px; text-align: center; margin: 20px 0;">
//                        <span style="font-size: 24px; font-weight: bold; color: #007bff; letter-spacing: 5px;">%s</span>
//                    </div>
//                    <p><strong>Lưu ý:</strong></p>
//                    <ul>
//                        <li>Mã OTP có hiệu lực trong <strong>5 phút</strong></li>
//                        <li>Không chia sẻ mã này với ai khác</li>
//                        <li>Nếu không phải bạn yêu cầu, vui lòng bỏ qua email này</li>
//                    </ul>
//                    <p style="color: #666; font-size: 12px;">Thời gian gửi: %s</p>
//                    <hr style="margin: 20px 0;">
//                    <p style="color: #999; font-size: 12px;">Email này được gửi tự động, vui lòng không reply.</p>
//                </div>
//            </body>
//            </html>
//            """, otp, currentTime);
//    }
//}
