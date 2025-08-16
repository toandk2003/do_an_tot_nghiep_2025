package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpRequestDto;
import org.yenln8.ChatApp.entity.AccountPending;
import org.yenln8.ChatApp.entity.LimitResource;
import org.yenln8.ChatApp.entity.OTP;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.AccountPendingRepository;
import org.yenln8.ChatApp.repository.LimitResourceRepository;
import org.yenln8.ChatApp.repository.OTPRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.VerifyOtpRegisterService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class VerifyOtpRegisterServiceImpl implements VerifyOtpRegisterService {
    private UserRepository userRepository;
    private OTPRepository otpRepository;
    private AccountPendingRepository accountPendingRepository;
    private LimitResourceRepository limitResourceRepository;

    @Override
    public BaseResponseDto call(VerifyOtpRequestDto form, HttpServletRequest request) throws Exception {
        OTP otp = validate(form, request);

        save(form, otp, request);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(MessageBundle.getMessage("app.email.register.success"))
                .build();
    }

    private OTP validate(VerifyOtpRequestDto form, HttpServletRequest request) {
        // Tim ban ghi ung voi OTP trong bang OTP va type REGISTER_ACCOUNT, neu khong ton tai hoac ton tai nhung het han, nem loi
        String otpCode = form.getOtp();

        Optional<OTP> optionalOTP = this.otpRepository.findByOtpCodeAndTypeAndDeletedAtIsNull(otpCode, OTP.TYPE.REGISTER_ACCOUNT);

        // Validate exist
        if (optionalOTP.isEmpty()) throw new IllegalArgumentException(MessageBundle.getMessage("error.otp.incorrect"));

        OTP otp = optionalOTP.get();
        log.info("OTP code {} found", otp);

        // Validate expired
        if (otp.getExpireAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException(MessageBundle.getMessage("error.otp.expire"));

        return otp;
    }

    private void save(VerifyOtpRequestDto form, OTP otp, HttpServletRequest request) {
        // Cap nhat OTP tu trang thai BE_SENT thanh VERIFIED va deletedAt = now()  + deleted = id
        otp.setStatus(OTP.STATUS.VERIFIED);
        otp.setDeletedAt(LocalDateTime.now());
        otp.setDeleted(otp.getId());

        this.otpRepository.save(otp);

        // Cap nhat AccountPending
        Optional<AccountPending> optionalAccountPending = this.accountPendingRepository.findByOtpIdAndStatusAndDeletedAtIsNull(otp.getId(), AccountPending.STATUS.PENDING);
        log.info("Account Pending: {}", optionalAccountPending);

        if (optionalAccountPending.isEmpty())
            throw new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "AccountPending", "otpId", otp.getId()));

        AccountPending accountPending = optionalAccountPending.get();
        accountPending.setStatus(AccountPending.STATUS.DONE);
        accountPending.setDeletedAt(LocalDateTime.now());
        accountPending.setDeleted(accountPending.getId());
        this.accountPendingRepository.save(accountPending);

        // Them User
        User newUser = this.userRepository.save(User.builder()
                .fullName(accountPending.getFullName())
                .email(accountPending.getEmail())
                .password(accountPending.getPassword())
                .role(accountPending.getRole())
                .status(User.STATUS.NO_ONBOARDING)
                .build());

        //Them limit resource cho user
        LimitResource limitResource = LimitResource.builder()
                .maxLimit(S3Constant.MAX_LIMIT_RESOURCE)
                .type(LimitResource.TYPE.MEDIA)
                .currentUsage(0L)
                .userId(newUser.getId())
                .build();
        this.limitResourceRepository.save(limitResource);

        log.info("new User : {}", newUser);
    }
}
