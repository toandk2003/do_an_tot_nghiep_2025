package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;
import org.yenln8.ChatApp.service.AuthService;

@Controller
@AllArgsConstructor
@NoArgsConstructor
public class AuthController {
    private AuthService authService;
}
