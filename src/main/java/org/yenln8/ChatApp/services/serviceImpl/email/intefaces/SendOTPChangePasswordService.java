package org.yenln8.ChatApp.services.serviceImpl.email.intefaces;

public interface SendOTPChangePasswordService {
    void call(String recipientEmail, String otp);
}
