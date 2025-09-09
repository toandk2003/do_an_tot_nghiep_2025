package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yenln8.ChatApp.entity.Notification;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class NotificationConvertedFromEntityDto implements Serializable {
    private Long id;
    private String content;
    private Notification.TYPE type;
    private Notification.STATUS status;
    private LocalDateTime seenAt;
    private LocalDateTime createdAt;
    private GetProfileResponseDto userReference;
}
