package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yenln8.ChatApp.entity.FriendRequest;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class GetListFriendRequestReceivedResponseDto implements Serializable {
    private Long id;
    private GetProfileResponseDto sender;
    private FriendRequest.STATUS status;
    private LocalDateTime sentAt;
}
