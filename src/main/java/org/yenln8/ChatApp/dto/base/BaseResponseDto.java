package org.yenln8.ChatApp.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  // Bỏ qua các trường null
public class BaseResponseDto {
    private Boolean success;
    private Integer statusCode;
    private String message;
}
