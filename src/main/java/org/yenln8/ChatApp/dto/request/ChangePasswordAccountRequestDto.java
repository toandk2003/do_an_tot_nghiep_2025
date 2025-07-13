package org.yenln8.ChatApp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChangePasswordAccountRequestDto {
    private String email;
    private String oldPassword;
    private String newPassword;
}
