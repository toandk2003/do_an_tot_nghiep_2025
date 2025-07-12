package org.yenln8.ChatApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyOTPResponseDto  {
    private Boolean success;
    private Integer statusCode;
    private String message;
}
