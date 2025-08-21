package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yenln8.ChatApp.entity.FriendRequest;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class RejectFriendResponseDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private FriendRequest.STATUS status;
}
