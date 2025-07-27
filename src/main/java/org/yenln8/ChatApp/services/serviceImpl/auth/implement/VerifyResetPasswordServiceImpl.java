package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpRequestDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpResetPasswordRequestDto;
import org.yenln8.ChatApp.entity.OTP;
import org.yenln8.ChatApp.entity.PasswordPending;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.OTPRepository;
import org.yenln8.ChatApp.repository.PasswordPendingRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.LogOutService;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.RegisterService;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.VerifyResetPasswordService;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class VerifyResetPasswordServiceImpl implements VerifyResetPasswordService {
    private UserRepository userRepository;
    private OTPRepository otpRepository;
    private PasswordPendingRepository passwordPendingRepository;
    private PasswordEncoder passwordEncoder;
    private LogOutService logOutService;
    private RegisterServiceImpl registerService;

    @Override
    public BaseResponseDto call(VerifyOtpResetPasswordRequestDto form, HttpServletRequest request) {
        OTP otp = validate(form, request);

        save(form, otp, request);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(MessageBundle.getMessage("app.email.reset.password.success"))
                .build();
    }

    private OTP validate(VerifyOtpResetPasswordRequestDto form, HttpServletRequest request) {

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

        // Tim ban ghi ung voi OTP trong bang OTP va type REGISTER_ACCOUNT, neu khong ton tai hoac ton tai nhung het han, nem loi
        String otpCode = form.getOtp();

        Optional<OTP> optionalOTP = this.otpRepository.findByOtpCodeAndTypeAndDeletedAtIsNull(otpCode, OTP.TYPE.FORGOT_PASSWORD);

        // Validate exist
        if (optionalOTP.isEmpty()) throw new IllegalArgumentException(MessageBundle.getMessage("error.otp.incorrect"));

        OTP otp = optionalOTP.get();
        log.info("OTP code {} found", otp);

        // Validate expired
        if (otp.getExpireAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.otp.expire"));

        return otp;
    }

    private void save(VerifyOtpResetPasswordRequestDto form, OTP otp, HttpServletRequest request) {
        // Cap nhat OTP tu trang thai BE_SENT thanh VERIFIED va deletedAt = now()  + deleted = id
        otp.setStatus(OTP.STATUS.VERIFIED);
        otp.setDeletedAt(LocalDateTime.now());
        otp.setDeleted(otp.getId());

        this.otpRepository.save(otp);

        // Cap nhat PasswordPending
        Optional<PasswordPending> optionalPasswordPending = this.passwordPendingRepository.findByOtpIdAndStatusAndDeletedAtIsNull(otp.getId(), PasswordPending.STATUS.PENDING);
        log.info("Password Pending: {}", optionalPasswordPending);

        if (optionalPasswordPending.isEmpty())
            throw new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "PasswordPending", "otpId", otp.getId()));

        PasswordPending passwordPending = optionalPasswordPending.get();
        passwordPending.setStatus(PasswordPending.STATUS.DONE);
        passwordPending.setDeletedAt(LocalDateTime.now());
        passwordPending.setDeleted(passwordPending.getId());
        this.passwordPendingRepository.save(passwordPending);

        // Update password

        User user = this.userRepository.findById(passwordPending.getUser().getId()).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "PasswordPending", "otpId", otp.getId())));
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));

        this.userRepository.save(user);
        log.info("new User : {}", user);

        // logout all device
        this.logOutService.logOutAllDevice(user.getId());
    }
}
