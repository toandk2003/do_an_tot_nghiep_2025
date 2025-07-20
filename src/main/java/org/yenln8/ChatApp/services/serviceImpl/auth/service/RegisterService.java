package org.yenln8.ChatApp.services.serviceImpl.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.common.constant.EmailConstant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.common.util.Network;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.RegisterAccountRequestDto;
import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;
import org.yenln8.ChatApp.entity.AccountPending;
import org.yenln8.ChatApp.entity.BlackListSendEmail;
import org.yenln8.ChatApp.entity.OTP;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.AccountPendingRepository;
import org.yenln8.ChatApp.repository.BlackListSendEmailRepository;
import org.yenln8.ChatApp.repository.OTPRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.SendOTPRegistrationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class RegisterService {
    private UserRepository userRepository;
    private OTPRepository OTPRepository;
    private BlackListSendEmailRepository blackListSendEmailRepository;
    private AccountPendingRepository accountPendingRepository;
    private SendOTPRegistrationService sendOTPRegistrationService;
    private PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto call(RegisterAccountRequestDto form, HttpServletRequest request) throws Exception {
        validate(form, request);
        save(form, request);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(MessageBundle.getMessage("app.otp.registration.otp.sent"))
                .build();
    }

    private void validate(RegisterAccountRequestDto form, HttpServletRequest request) {
        String email = form.getEmail();
        String password = form.getPassword();

        // - password it nhat 1 ki tu so
        if (!this.containsDigit(password))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.password.contains.digit"));

        // + password it nhat 1 ki tu chu cai viet thuong
        if (!this.containsLowercase(password))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.password.contains.lowercase"));

        // + password it nhat 1 ki tu chu cai viet hoa
        if (!this.containsUppercase(password))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.password.contains.uppercase"));

        // + password it nhat 1 ki tu dac biet thuoc !@#$%^&*()_+-=[]{};\\':\"|,./<>?`~
        if (!this.containsSpecialChar(password))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.password.contains.character"));

        // Kiem tra email da duoc dang ky hay chua(co the dang bi LOCK, BAN or ACTIVE), live= ACTIVE + LOCK + BAN
        Optional<User> user = this.userRepository.findByEmailAndDeletedAtIsNull(email);

        if (user.isPresent()) throw new IllegalArgumentException(MessageBundle.getMessage("error.email.exists"));

        // Kiem tra user co bi cam gui email dang ky tai khoan khong
        Optional<BlackListSendEmail> blackListSendEmail = this.blackListSendEmailRepository.findByFromIpAddressAndToEmailAndType(Network.getUserIP(request), email, BlackListSendEmail.TYPE.REGISTER_ACCOUNT);

        if (blackListSendEmail.isPresent() &&
                blackListSendEmail.get().getStatus().equals(BlackListSendEmail.STATUS.BAN) &&
                blackListSendEmail.get().getFreeToSendAt().isAfter(LocalDateTime.now())
        ) {
            throw new IllegalArgumentException(MessageBundle.getMessage("error.email.exceed"));
        }
    }

    private void save(RegisterAccountRequestDto form, HttpServletRequest request) throws Exception {
        String email = form.getEmail();
        String password = form.getPassword();
        String firstName = form.getFirstName();
        String lastName = form.getLastName();
        String fullName = firstName + " " + lastName;
        String ipAddress = Network.getUserIP(request);


        // Luu thong tin OTP tao duoc vao bang OTP
        OTP otp = this.OTPRepository.save(OTP.builder()
                .type(OTP.TYPE.REGISTER_ACCOUNT)
                .otpCode(OTP.generateOTP())
                .fromIpAddress(ipAddress)
                .rowVersion(0)
                .toEmail(email)
                .expireAt(LocalDateTime.now().plusMinutes(EmailConstant.EXPIRE_TIME_IN_MINUTES))
                .status(OTP.STATUS.BE_SENT)
                .build());
        log.info("otp saved: {}", otp);

        // Luu thong tin vao bang Account Pending
         AccountPending accountPending = this.accountPendingRepository.save(AccountPending.builder()
                .otpId(otp.getId())
                .role(User.ROLE.USER)
                .status(AccountPending.STATUS.PENDING)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(fullName)
                .password(passwordEncoder.encode(password))
                .build());
        log.info("AccountPending saved: {}", accountPending);

        // Kiem tra so lan gui email tu ip X toi email Y voi type = Z, neu dat threshhold, tien hanh cap nhat bang BlackList de cam gui email tiep
        validateReachLimitSendEmail(ipAddress,email);

        // Gui email
        SendEmailResponseDto emailResponse = this.sendOTPRegistrationService.sendOTPRegistration(email, otp.getOtpCode());
        log.info("emailResponse register: {}", emailResponse);

        if (!emailResponse.getSuccess()) throw new Exception(MessageBundle.getMessage("error.system.send.mail"));
    }

    private void validateReachLimitSendEmail(String ipAddress, String email) {
        long countOtpSent = this.OTPRepository.countByFromIpAddressAndToEmailAndTypeAndCreatedAtAfterAndDeletedAtIsNull(ipAddress, email, OTP.TYPE.REGISTER_ACCOUNT, LocalDateTime.now().minusMinutes(EmailConstant.LIMIT_MINUTES_TO_SEND_EMAIL));
        log.info("countOtpSent to check exceed: {}", countOtpSent);

        if (countOtpSent < EmailConstant.LIMIT_QUANTITY_EMAIL_SENT) return;

        Optional<BlackListSendEmail> oBlackListSendEmail = this.blackListSendEmailRepository.findByFromIpAddressAndToEmailAndType(ipAddress, email, BlackListSendEmail.TYPE.REGISTER_ACCOUNT);

        if (oBlackListSendEmail.isEmpty()) {
            BlackListSendEmail blackListSendEmailSaved = this.blackListSendEmailRepository.save(BlackListSendEmail.builder()
                    .freeToSendAt(LocalDateTime.now().plusMinutes(EmailConstant.LIMIT_MINUTES_TO_SEND_EMAIL))
                    .toEmail(email)
                    .fromIpAddress(ipAddress)
                    .status(BlackListSendEmail.STATUS.BAN)
                    .type(BlackListSendEmail.TYPE.REGISTER_ACCOUNT)
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

    private boolean containsDigit(String s) {
        if (s == null) return false;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) return true;
        }

        return false;
    }

    private boolean containsUppercase(String s) {
        if (s == null) return false;
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) return true;
        }

        return false;
    }

    private boolean containsLowercase(String s) {
        if (s == null) return false;
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) return true;
        }

        return false;
    }

    public boolean containsSpecialChar(String s) {
        if (s == null) return false;

        String specialChars = "!@#$%^&*()_+-=[]{};\\':\"|,./<>?`~";
        for (char c : s.toCharArray()) {
            if (specialChars.indexOf(c) >= 0) return true;
        }

        return false;
    }
}
