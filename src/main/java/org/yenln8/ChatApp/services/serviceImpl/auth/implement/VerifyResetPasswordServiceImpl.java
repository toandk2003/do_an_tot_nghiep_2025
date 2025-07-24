package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.spam.SpamService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpRequestDto;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.VerifyResetPasswordService;

@Slf4j
@AllArgsConstructor
@Service
public class VerifyResetPasswordServiceImpl implements VerifyResetPasswordService {
    private UserRepository userRepository;
    private OTPRepository OTPRepository;
    private BlackListSendEmailRepository blackListSendEmailRepository;
    private AccountPendingRepository accountPendingRepository;
    private PasswordEncoder passwordEncoder;
    private EmailOutboxRepository emailOutboxRepository;
    private SpamService spamService;

    @Override
    public BaseResponseDto call(VerifyOtpRequestDto form, HttpServletRequest request) {
//        User user = validate(loginRequestDto, request);

//        String token = save(user,request);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("OK")
                .build();
    }

    private void validate(VerifyOtpRequestDto form, HttpServletRequest request) {


    }

    private void save(User user, HttpServletRequest request) {

    }
}
