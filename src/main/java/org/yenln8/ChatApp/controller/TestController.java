package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.SendEmailResponseDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.service.EmailService;

@RestController
@AllArgsConstructor
public class TestController {
    private EmailService emailService;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        SendEmailResponseDto sendEmailResponseDto = this.emailService.systemSendTo("dieutuyetnguyenthi@gmail.com", "xinCHao", "hihi");
        System.out.println(sendEmailResponseDto);
        return ResponseEntity.ok(sendEmailResponseDto);
    }
}
