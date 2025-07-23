package org.yenln8.ChatApp.common.util.spam.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.EmailConstant;
import org.yenln8.ChatApp.entity.BlackListSendEmail;
import org.yenln8.ChatApp.entity.OTP;
import org.yenln8.ChatApp.repository.BlackListSendEmailRepository;
import org.yenln8.ChatApp.repository.OTPRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class AvoidSpamOTPService {
    private OTPRepository otpRepository;
    private BlackListSendEmailRepository blackListSendEmailRepository;

    public void call(String ipAddress, String email, BlackListSendEmail.TYPE blackListType) {
        OTP.TYPE otpType = this.getOTPTypeFromBlackListType(blackListType);

        long countOtpSent = this.otpRepository.countByFromIpAddressAndToEmailAndTypeAndCreatedAtAfterAndDeletedAtIsNull(ipAddress, email, otpType, LocalDateTime.now().minusMinutes(EmailConstant.LIMIT_MINUTES_TO_SEND_EMAIL));
        log.info("countOtpSent to check exceed: {}", countOtpSent);

        if (countOtpSent < EmailConstant.LIMIT_QUANTITY_EMAIL_SENT) return;

        Optional<BlackListSendEmail> oBlackListSendEmail = this.blackListSendEmailRepository.findByFromIpAddressAndToEmailAndType(ipAddress, email, blackListType);

        if (oBlackListSendEmail.isEmpty()) {
            BlackListSendEmail blackListSendEmailSaved = this.blackListSendEmailRepository.save(BlackListSendEmail.builder()
                    .freeToSendAt(LocalDateTime.now().plusMinutes(EmailConstant.LIMIT_MINUTES_TO_SEND_EMAIL))
                    .toEmail(email)
                    .fromIpAddress(ipAddress)
                    .status(BlackListSendEmail.STATUS.BAN)
                    .type(blackListType)
                    .build());

            log.info("blackListSendEmailSaved is created: {}", blackListSendEmailSaved);

        } else {
            BlackListSendEmail blackListSendEmail = oBlackListSendEmail.get();
            blackListSendEmail.setFreeToSendAt(LocalDateTime.now().plusMinutes(EmailConstant.LIMIT_MINUTES_TO_SEND_EMAIL));
            blackListSendEmail.setStatus(BlackListSendEmail.STATUS.BAN);

            BlackListSendEmail blackListSendEmailSaved = this.blackListSendEmailRepository.save(blackListSendEmail);
            log.info("blackListSendEmailSaved is updated: {}", blackListSendEmailSaved);
        }

    }


    private OTP.TYPE getOTPTypeFromBlackListType(BlackListSendEmail.TYPE type) {
        return switch (type) {
            case REGISTER_ACCOUNT -> OTP.TYPE.REGISTER_ACCOUNT;
            case CHANGE_PASSWORD -> OTP.TYPE.CHANGE_PASSWORD;
            case FORGOT_PASSWORD -> OTP.TYPE.FORGOT_PASSWORD;
            default -> throw new IllegalArgumentException("Invalid OTP TYPE");
        };
    }
}
