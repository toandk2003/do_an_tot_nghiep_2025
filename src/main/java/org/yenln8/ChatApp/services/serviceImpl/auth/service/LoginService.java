package org.yenln8.ChatApp.services.serviceImpl.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yenln8.ChatApp.common.constant.AuthConstant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.common.util.Network;
import org.yenln8.ChatApp.common.util.spam.SpamService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.redis.BlackListLoginDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.security.JwtTokenProvider;
import org.yenln8.ChatApp.services.RedisService;

import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
@Service
public class LoginService {
    private RedisService redisService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private SpamService spamService;

    public BaseResponseDto call(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        User user = validate(loginRequestDto, request);

        String token = save(user,request);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(token)
                .build();
    }

    private User validate(LoginRequestDto form, HttpServletRequest request) {

        BlackListLoginDto blackListLoginDto = this.redisService.getKey(this.redisService.getKeyLoginWithPrefix(form.getEmail(), Network.getUserIP(request)), BlackListLoginDto.class);
        // Kiem lan login vuot qua muc cho phep
        if (blackListLoginDto != null && blackListLoginDto.getBan().equals(Boolean.TRUE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MessageBundle.getMessage("error.login.fail.reach.limit", AuthConstant.LOGIN_TRY_AGAIN_TIME_IN_MINUTES));
        }

        // Lay ra thong tin user trong db, so sanh tai khoan + pass
        Optional<User> optionalUser = this.userRepository.findByEmailAndDeletedAtIsNull(form.getEmail());
        if (optionalUser.isEmpty()) {
            // +1 fail login
            this.spamService.avoidSpamLogin(form, request, blackListLoginDto);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageBundle.getMessage("error.login.fail"));

        }

        User user = optionalUser.get();
        if (!(user.getEmail().equals(form.getEmail()) && passwordEncoder.matches(form.getPassword(), user.getPassword()))) {
            // +1 fail login
            this.spamService.avoidSpamLogin(form, request, blackListLoginDto);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageBundle.getMessage("error.login.fail"));
        }

        // Kiem tra tai khoan co bi LOCK hoac BAN khong
        if (user.getStatus().equals(User.STATUS.LOCK)) {
            // +1 fail login
            this.spamService.avoidSpamLogin(form, request, blackListLoginDto);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MessageBundle.getMessage("error.login.lock"));
        }
        if (user.getStatus().equals(User.STATUS.BAN)) {
            // +1 fail login
            this.spamService.avoidSpamLogin(form, request, blackListLoginDto);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MessageBundle.getMessage("error.login.ban"));
        }

        return user;
    }

    private String save(User user, HttpServletRequest request) {
        Long id = user.getId();
        String email = user.getEmail();
        User.ROLE role = user.getRole();

        //delete key redis
        this.redisService.deleteKey(this.redisService.getKeyLoginWithPrefix(user.getEmail(), Network.getUserIP(request)));

        //create token return
        return this.jwtTokenProvider.createToken(id, email, Collections.singletonList(role.name()));
    }
}
