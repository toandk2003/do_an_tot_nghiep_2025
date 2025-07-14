package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.service.AuthService;

@RestController
@AllArgsConstructor
@NoArgsConstructor
public class AuthController {
    private AuthService authService;

    @GetMapping("/login")
    public BaseResponseDto login(LoginRequestDto form) {
        // Lay ra thong tin tai acc + pass tu db
        // check equal , khaonnnnn
        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .message(MessageBundle.getMessage("login.success"))
                .build();
    }
}
