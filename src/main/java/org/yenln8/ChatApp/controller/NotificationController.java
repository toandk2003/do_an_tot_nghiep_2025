package org.yenln8.ChatApp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yenln8.ChatApp.dto.request.ChangeStatusNotificationRequestDto;
import org.yenln8.ChatApp.dto.request.GetNotificationRequestDto;
import org.yenln8.ChatApp.services.interfaces.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
@Validated
public class NotificationController {
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotification(GetNotificationRequestDto form) {
        return ResponseEntity.ok(this.notificationService.getNotification(form));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable("id")
                                          @Min(0)
                                          @Max(Long.MAX_VALUE)
                                          @NotNull
                                          Long notificationId,
                                          @Valid @RequestBody ChangeStatusNotificationRequestDto form) {
        return ResponseEntity.ok(this.notificationService.changeStatusNotification(notificationId,form));
    }
}
