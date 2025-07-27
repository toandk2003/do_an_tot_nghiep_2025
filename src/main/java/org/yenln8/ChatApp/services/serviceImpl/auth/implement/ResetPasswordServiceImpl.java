package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.EmailConstant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.common.util.Network;
import org.yenln8.ChatApp.common.util.spam.SpamService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ResetPasswordAccountRequestDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.ResetPasswordService;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {
    private UserRepository userRepository;
    private OTPRepository OTPRepository;
    private BlackListSendEmailRepository blackListSendEmailRepository;
    private AccountPendingRepository accountPendingRepository;
    private PasswordEncoder passwordEncoder;
    private EmailOutboxRepository emailOutboxRepository;
    private SpamService spamService;
    private RegisterServiceImpl registerService;
    private PasswordPendingRepository passwordPendingRepository;

    @Override
    public BaseResponseDto call(ResetPasswordAccountRequestDto form, HttpServletRequest request) {
        validate(form, request);

        save(form, request);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(MessageBundle.getMessage("app.otp.registration.otp.sent"))
                .build();
    }

    private void validate(ResetPasswordAccountRequestDto form, HttpServletRequest request) {
        String email = form.getEmail();

        // kiem tra co spam email k
        Optional<BlackListSendEmail> blackListSendEmail = this.blackListSendEmailRepository.findByFromIpAddressAndToEmailAndType(Network.getUserIP(request), email, BlackListSendEmail.TYPE.FORGOT_PASSWORD);

        if (blackListSendEmail.isPresent() &&
                blackListSendEmail.get().getStatus().equals(BlackListSendEmail.STATUS.BAN) &&
                blackListSendEmail.get().getFreeToSendAt().isAfter(LocalDateTime.now())
        ) {
            throw new IllegalArgumentException(MessageBundle.getMessage("error.email.exceed", EmailConstant.LIMIT_MINUTES_TO_SEND_EMAIL));
        }
    }

    private void save(ResetPasswordAccountRequestDto form, HttpServletRequest request) {
        String email = form.getEmail().trim();
        String ipAddress = Network.getUserIP(request);

        // Luu thong tin OTP tao duoc vao bang OTP
        OTP otp = this.OTPRepository.save(OTP.builder()
                .type(OTP.TYPE.FORGOT_PASSWORD)
                .otpCode(OTP.generateOTP())
                .fromIpAddress(ipAddress)
                .rowVersion(0)
                .toEmail(email)
                .expireAt(LocalDateTime.now().plusMinutes(EmailConstant.EXPIRE_TIME_IN_MINUTES))
                .status(OTP.STATUS.BE_SENT)
                .build());
        log.info("otp saved: {}", otp);

        User user = this.userRepository.findByEmailAndDeletedAtIsNull(form.getEmail()).orElseThrow();

        // Luu thong tin vao bang Password Pending
        PasswordPending passwordPending = this.passwordPendingRepository.save(PasswordPending.builder()
                .otpId(otp.getId())
                .newPassword("")
                .user(user)
                .type(PasswordPending.TYPE.FORGOT)
                .status(PasswordPending.STATUS.PENDING)
                .build());
        log.info("passwordPending saved: {}", passwordPending);

        // Kiem tra so lan gui email tu ip X toi email Y voi type = Z, neu dat threshhold, tien hanh cap nhat bang BlackList de cam gui email tiep
        this.spamService.avoidSpamOTP(ipAddress, email, BlackListSendEmail.TYPE.FORGOT_PASSWORD);

        // Gui email
        EmailOutbox emailOutboxSaved = this.emailOutboxRepository.save(EmailOutbox.builder()
                .toEmail(email)
                .otpCode(otp.getOtpCode())
                .type(EmailOutbox.TYPE.FORGOT_PASSWORD)
                .build());
        log.info("EmailOutbox saved: {}", emailOutboxSaved);

    }
}
