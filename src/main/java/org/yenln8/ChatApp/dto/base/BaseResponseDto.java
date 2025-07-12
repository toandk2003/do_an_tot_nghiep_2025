package org.yenln8.ChatApp.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BaseResponseDto {
    private Boolean success;
    private Integer statusCode;
    private String message;
}
