package org.yenln8.ChatApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.security.JwtTokenProvider;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class TestController {
    private JwtTokenProvider jwtTokenProvider;
    private EmailService emailService;
    private RedisTemplate<String, Object> redisTemplate;
    private UserRepository  userRepository;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
        String token = jwtTokenProvider.createToken(1L, "abc@mgila.com", List.of("ADMIN"));
        return ResponseEntity.ok(token);
    }

    @GetMapping("/tevst-exception")
    public ResponseEntity<?> exceptions(HttpServletRequest request) throws Exception {
//        emailService.systemSendTo("ddfdfdfdfddfffffffsaaaa@gmail.com", "verrify", "abccc");
//        System.out.println(Network.getUserIP(request));

//        redisTemplate.opsForValue().set("lll", new int[]{1, 2, 3});
//        throw new Exception(MessageBundle.getMessage("error.system.send.mail"));
//        System.out.println(redisTemplate.opsForValue().get("exception"));
//        return ResponseEntity.ok(1);
        Optional<User> optionalUserser = this.userRepository.findById(1L);
        if(optionalUserser.isPresent()) {
            User user = optionalUserser.get();
            user.setFullName("hihihihi");
            User userSaved = this.userRepository.save(user);
            return ResponseEntity.ok(userSaved);
        }
        return ResponseEntity.ok(optionalUserser);
    }

}
