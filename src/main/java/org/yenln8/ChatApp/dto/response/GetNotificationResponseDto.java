package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yenln8.ChatApp.dto.base.PaginationResponseDto;
import org.yenln8.ChatApp.entity.Notification;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class GetNotificationResponseDto implements Serializable {
    private Long notSeenNotificationNums;
    private PaginationResponseDto<Notification, NotificationConvertedFromEntityDto> paginationInfo;
}
