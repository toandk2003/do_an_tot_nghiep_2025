package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yenln8.ChatApp.common.constant.EmailConstant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.common.util.Network;
import org.yenln8.ChatApp.common.util.spam.SpamService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.ChangePasswordAccountRequestDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.ChangePasswordService;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.RegisterService;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ChangePasswordServiceImpl implements ChangePasswordService {
    private UserRepository userRepository;
    private OTPRepository OTPRepository;
    private BlackListSendEmailRepository blackListSendEmailRepository;
    private PasswordEncoder passwordEncoder;
    private EmailOutboxRepository emailOutboxRepository;
    private SpamService spamService;
    private RegisterServiceImpl registerService;
    private PasswordPendingRepository passwordPendingRepository;

    @Override
    public BaseResponseDto call(ChangePasswordAccountRequestDto form, HttpServletRequest request) {
        User user = validate(form, request);

        save(user, form, request);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(MessageBundle.getMessage("app.otp.registration.otp.sent"))
                .build();
    }

    private User validate(ChangePasswordAccountRequestDto form, HttpServletRequest request) {
        Authentication securityContextHolder = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) securityContextHolder.getPrincipal();

        String email = currentUser.getEmail();
        String oldPassword = form.getOldPassword();
        String newPassword = form.getNewPassword();

        // kiem tra password moi co hop format khong
        // - password it nhat 1 ki tu so
        if (!registerService.containsDigit(newPassword))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.password.contains.digit"));

        // + password it nhat 1 ki tu chu cai viet thuong
        if (!registerService.containsLowercase(newPassword))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.password.contains.lowercase"));

        // + password it nhat 1 ki tu chu cai viet hoa
        if (!registerService.containsUppercase(newPassword))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.password.contains.uppercase"));

        // + password it nhat 1 ki tu dac biet thuoc !@#$%^&*()_+-=[]{};\\':\"|,./<>?`~
        if (!registerService.containsSpecialChar(newPassword))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.password.contains.character"));

        // kiem tra email + pass co dung khong
        User user = this.userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageBundle.getMessage("error.login.fail")));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageBundle.getMessage("error.old.password.incorrect"));

        }
        // kiem tra co spam email k
        Optional<BlackListSendEmail> blackListSendEmail = this.blackListSendEmailRepository.findByFromIpAddressAndToEmailAndType(Network.getUserIP(request), email, BlackListSendEmail.TYPE.CHANGE_PASSWORD);

        if (blackListSendEmail.isPresent() &&
                blackListSendEmail.get().getStatus().equals(BlackListSendEmail.STATUS.BAN) &&
                blackListSendEmail.get().getFreeToSendAt().isAfter(LocalDateTime.now())
        ) {
            throw new IllegalArgumentException(MessageBundle.getMessage("error.email.exceed", EmailConstant.LIMIT_MINUTES_TO_SEND_EMAIL));
        }

        return user;
    }

    private void save(User user, ChangePasswordAccountRequestDto form, HttpServletRequest request) {
        String email = user.getEmail().trim();
        String newPassword = form.getNewPassword();
        String ipAddress = Network.getUserIP(request);

        // Luu thong tin OTP tao duoc vao bang OTP
        OTP otp = this.OTPRepository.save(OTP.builder()
                .type(OTP.TYPE.CHANGE_PASSWORD)
                .otpCode(OTP.generateOTP())
                .fromIpAddress(ipAddress)
                .rowVersion(0)
                .toEmail(email)
                .expireAt(LocalDateTime.now().plusMinutes(EmailConstant.EXPIRE_TIME_IN_MINUTES))
                .status(OTP.STATUS.BE_SENT)
                .build());
        log.info("otp saved: {}", otp);

        // Luu thong tin vao bang Password Pending
        PasswordPending passwordPending = this.passwordPendingRepository.save(PasswordPending.builder()
                .otpId(otp.getId())
                .newPassword(newPassword)
                .user(user)
                .type(PasswordPending.TYPE.CHANGE)
                .status(PasswordPending.STATUS.PENDING)
                .build());
        log.info("passwordPending saved: {}", passwordPending);

        // Kiem tra so lan gui email tu ip X toi email Y voi type = Z, neu dat threshhold, tien hanh cap nhat bang BlackList de cam gui email tiep
        this.spamService.avoidSpamOTP(ipAddress, email, BlackListSendEmail.TYPE.CHANGE_PASSWORD);

        // Gui email
        EmailOutbox emailOutboxSaved = this.emailOutboxRepository.save(EmailOutbox.builder()
                .toEmail(email)
                .otpCode(otp.getOtpCode())
                .type(EmailOutbox.TYPE.CHANGE_PASSWORD)
                .build());
        log.info("EmailOutbox saved: {}", emailOutboxSaved);

    }
}
