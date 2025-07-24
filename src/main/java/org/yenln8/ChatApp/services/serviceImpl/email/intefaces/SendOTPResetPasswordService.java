package org.yenln8.ChatApp.services.serviceImpl.email.intefaces;

public interface SendOTPResetPasswordService {
    void call(String recipientEmail, String otp);
}
