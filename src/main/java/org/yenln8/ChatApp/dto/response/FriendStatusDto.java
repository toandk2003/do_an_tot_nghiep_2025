package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class FriendStatusDto {
    private Long userIdInMySql;
    private String email;
    private boolean isFriend;
    private boolean isNotFriendAndNoSentFriendRequest;
    private boolean isNotFriendAndSentFriendRequest;
}
