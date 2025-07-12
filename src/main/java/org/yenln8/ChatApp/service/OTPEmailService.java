//package org.yenln8.ChatApp.service;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OTPEmailService {
//
//    @Value("${email.sender.address}")
//    private String senderEmail;
//
//    @Value("${email.sender.password}")
//    private String senderPassword;
//
//    private OTPService otpService;
//
//    @PostConstruct
//    public void init() {
//        this.otpService = new OTPService(senderEmail, senderPassword);
//    }
//
//    public boolean sendOTP(String email) {
//        return otpService.sendOTP(email);
//    }
//
//    public boolean verifyOTP(String email, String otp) {
//        return otpService.verifyOTP(email, otp);
//    }
//}