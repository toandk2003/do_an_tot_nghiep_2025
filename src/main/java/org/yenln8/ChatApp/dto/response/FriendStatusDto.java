package org.yenln8.ChatApp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class FriendStatusDto {
    private Long userIdInMySql;
    private String email;
    @JsonProperty("isFriend")
    private boolean isFriend;
    @JsonProperty("isSendFriendRequest")
    private boolean isSendFriendRequest;
}
