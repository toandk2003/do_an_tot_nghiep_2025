package org.yenln8.ChatApp.common.util.spam;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.redis.BlackListLoginDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.entity.BlackListSendEmail;

public interface SpamService {
    public void avoidSpamLogin(LoginRequestDto form, HttpServletRequest request, BlackListLoginDto blackListLoginDto);

    public void avoidSpamOTP(String ipAddress, String email, BlackListSendEmail.TYPE blackListType);
}
