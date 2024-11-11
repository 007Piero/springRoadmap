package com.springpiero.UserAuth.piero.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    @Size(min = 6, max = 20)
    @Schema(description = "password", example = "p@ssW0rd",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20, minLength = 6)
    private String password;
}
