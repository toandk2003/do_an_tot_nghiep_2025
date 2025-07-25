package org.yenln8.ChatApp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RegisterAccountRequestDto {
    @Size(min = 1, max = 255)
    @Email
    private String email;

    @Size(min = 8, max = 16)
    private String password;

    @Size(min = 3, max = 20)
    @NotNull
    @NotBlank
    private String fullName;

}
