package org.yenln8.ChatApp.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  // Bỏ qua các trường null

public class BaseResponseDto {
    private Boolean success;
    private int statusCode;
    private String message;
}
