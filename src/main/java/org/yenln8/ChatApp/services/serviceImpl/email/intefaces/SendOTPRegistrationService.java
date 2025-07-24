package org.yenln8.ChatApp.services.serviceImpl.email.intefaces;

public interface SendOTPRegistrationService {
    void call(String recipientEmail, String otp);
}
