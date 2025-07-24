package org.yenln8.ChatApp.common.util.spam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.spam.service.AvoidSpamLoginService;
import org.yenln8.ChatApp.common.util.spam.service.AvoidSpamOTPService;
import org.yenln8.ChatApp.dto.redis.BlackListLoginDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.entity.BlackListSendEmail;

@Service
@Slf4j
@AllArgsConstructor
public class SpamServiceImpl implements SpamService {
    private AvoidSpamLoginService avoidSpamLoginService;
    private AvoidSpamOTPService avoidSpamOTPService;


    @Override
    public void avoidSpamLogin(LoginRequestDto form, HttpServletRequest request, BlackListLoginDto blackListLoginDto) {
        this.avoidSpamLoginService.call(form, request, blackListLoginDto);
    }

    @Override
    public void avoidSpamOTP(String ipAddress, String email, BlackListSendEmail.TYPE blackListType) {
        this.avoidSpamOTPService.call(ipAddress, email, blackListType);
    }
}
