package org.yenln8.ChatApp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.dto.request.UpdateProfileRequestDto;
import org.yenln8.ChatApp.services.interfaces.UserService;
import org.yenln8.ChatApp.services.serviceImpl.user.UserServiceImpl;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Validated
public class UserController {
    private UserService userService;
    private UserServiceImpl userServiceImpl;

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody @Valid UpdateProfileRequestDto form) throws Exception {
        return ResponseEntity.ok(this.userService.updateProfile(form));
    }

    @GetMapping("/explore")
    public ResponseEntity<?> explore(ExploreRequestDto form) {
        return ResponseEntity.ok(this.userService.explore(form));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam("email") String email) {
        return ResponseEntity.ok(this.userServiceImpl.getDetail(email));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable("id")
                                       @Min(1)
                                       @Max(Long.MAX_VALUE)
                                       @NotNull
                                       Long userId) {
        return ResponseEntity.ok(this.userService.block(userId));
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<?> unblock(@PathVariable("id")
                                         @Min(1)
                                         @Max(Long.MAX_VALUE)
                                         @NotNull
                                         Long blockId) {
        return ResponseEntity.ok(this.userService.unblock(blockId));
    }

    @PostMapping(value = "/generate-presignedURL", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generatePresignURLUpdateProfile(@ModelAttribute @Valid UploadFileRequestDto form) throws Exception {
        return ResponseEntity.ok(this.userService.generatePresignedURLUpdateProfile(form));
    }
}
