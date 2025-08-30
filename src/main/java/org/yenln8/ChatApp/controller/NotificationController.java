package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.request.GetNotificationRequestDto;
import org.yenln8.ChatApp.services.interfaces.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
@Validated
public class NotificationController {
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotification( GetNotificationRequestDto form) {
        return ResponseEntity.ok(this.notificationService.getNotification(form));
    }
}
