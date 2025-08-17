package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.services.interfaces.UserService;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Validated
public class UserController {
    private UserService userService;

    @GetMapping("/explore")
    public ResponseEntity<?> explore(ExploreRequestDto form) {
        return ResponseEntity.ok(this.userService.explore(form));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<?> block(ExploreRequestDto form) {
        return ResponseEntity.ok(this.userService.block(form));
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<?> unblock(ExploreRequestDto form) {
        return ResponseEntity.ok(this.userService.unblock(form));
    }

}
