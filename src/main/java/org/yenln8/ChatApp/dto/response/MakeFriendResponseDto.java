package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yenln8.ChatApp.entity.FriendRequest;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class MakeFriendResponseDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private FriendRequest.STATUS status;
    private LocalDateTime sentAt;
    private boolean autoAccepted;
}
