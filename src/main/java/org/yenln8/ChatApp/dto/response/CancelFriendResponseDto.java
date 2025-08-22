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
public class CancelFriendResponseDto {
    private Long id;
    private GetProfileResponseDto sender;
    private GetProfileResponseDto receiver;
    private FriendRequest.STATUS status;
    private LocalDateTime sentAt;

}
