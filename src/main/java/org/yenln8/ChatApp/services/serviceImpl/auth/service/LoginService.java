package org.yenln8.ChatApp.services.serviceImpl.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yenln8.ChatApp.common.constant.AuthConstant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.redis.BlackListLoginDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.RedisService;

import java.util.Optional;

@AllArgsConstructor
@Service
public class LoginService {
    private RedisService redisService;
    private UserRepository userRepository;

    public BaseResponseDto call(LoginRequestDto loginRequestDto) {
        validate(loginRequestDto);

        String token = save(loginRequestDto);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(token)
                .build();
    }

    private void validate(LoginRequestDto form) {
        BlackListLoginDto blackListLoginDto = this.redisService.getKey(form.getEmail(), BlackListLoginDto.class);
        // Kiem lan login vuot qua muc cho phep
        if (blackListLoginDto != null && blackListLoginDto.getBan().equals(Boolean.TRUE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MessageBundle.getMessage("error.login.fail.reach.limit", AuthConstant.LOGIN_TRY_AGAIN_TIME_IN_MINUTES));
        }

        // Lay ra thong tin user trong db, so sanh tai khoan + pass
        Optional<User> optionalUser = this.userRepository.findByEmailAndDeletedAtIsNull(form.getEmail());
        if (optionalUser.isEmpty()) {
            // +1 fail login
            handleFailToLogin(form, blackListLoginDto);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageBundle.getMessage("error.login.fail"));

        }

        User user = optionalUser.get();
        if (!(user.getEmail().equals(form.getEmail()) && user.getPassword().equals(form.getPassword()))) {
            // +1 fail login
            handleFailToLogin(form, blackListLoginDto);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageBundle.getMessage("error.login.fail"));
        }

        // Kiem tra tai khoan co bi LOCK hoac BAN khong
        if (user.getStatus().equals(User.STATUS.LOCK)) {
            // +1 fail login
            handleFailToLogin(form, blackListLoginDto);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MessageBundle.getMessage("error.login.lock"));
        }
        if (user.getStatus().equals(User.STATUS.BAN)) {
            // +1 fail login
            handleFailToLogin(form, blackListLoginDto);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MessageBundle.getMessage("error.login.ban"));
        }
    }

    private void handleFailToLogin(LoginRequestDto form, BlackListLoginDto blackListLoginDto) {
        // key no exists
        if (blackListLoginDto == null) {
            this.redisService.setKeyInMinutes(form.getEmail(), BlackListLoginDto.builder()
                            .ban(Boolean.FALSE)
                            .failTimes(1)
                            .build(),
                    AuthConstant.LOGIN_TRY_AGAIN_TIME_IN_MINUTES);
        } else {
            // reach limit
            if (blackListLoginDto.getFailTimes() == AuthConstant.LOGIN_FAIL_LIMIT - 1) {
                this.redisService.setKeyInMinutes(form.getEmail(), BlackListLoginDto.builder()
                                .ban(Boolean.TRUE)
                                .build(),
                        AuthConstant.LOGIN_TRY_AGAIN_TIME_IN_MINUTES);
            } else {
                // reach limit yet
                blackListLoginDto.setFailTimes(blackListLoginDto.getFailTimes() + 1);
                this.redisService.updateValueKeepTTL(form.getEmail(), blackListLoginDto);
            }
        }
    }

    private String save(LoginRequestDto form) {
        //delete key redis
        //create token return
        // remember add last Online in redis in interceptor
        return null;
    }
}
