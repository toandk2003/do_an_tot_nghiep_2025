package org.yenln8.ChatApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.yenln8.ChatApp.common.util.MessageBundle;


@SpringBootApplication
@EnableScheduling
public class StrongStrongApplication {
    private static final Logger logger = LoggerFactory.getLogger(StrongStrongApplication.class);

    @Autowired
    @Value("${email.sender.password}")
    public static String passwordSender;

    public static void main(String[] args) {
        SpringApplication.run(StrongStrongApplication.class, args);
//        String emailSenderAddress = "ngocyenptit153@gmail.com";
//        String passwordSender = "byds fopg fczb aahn";
//        System.out.println("hoho: " + emailSenderAddress);
//        OTPService otpService = new OTPService(emailSenderAddress, passwordSender);
//
//        // Gửi OTP
//        String recipientEmail = "dieutuyetnguyenthi@gmail.com";
//        if (otpService.sendOTP(recipientEmail)) {
//            System.out.println("OTP sent successfully!");
//
//            // Simulate user input OTP
//            String userInputOTP = "123456"; // User nhập OTP
//
//            if (otpService.verifyOTP(recipientEmail, userInputOTP)) {
//                System.out.println("OTP verified successfully!");
//            } else {
//                System.out.println("Invalid or expired OTP!");
//            }
//        } else {
//            System.out.println("Failed to send OTP!");
//        }
        logger.info(MessageBundle.getMessage("app.email.register.otp.subject"));
    }
}
