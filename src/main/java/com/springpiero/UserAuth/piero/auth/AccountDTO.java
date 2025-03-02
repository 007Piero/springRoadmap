package com.springpiero.UserAuth.piero.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountDTO {

    @Email
    @Schema(description = "Email address", example = "jpierro@hotmail.fr", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(min = 6, max = 20)
    @Schema(description = "Password", example = "p@ssW0rd",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20, minLength = 6)
    private String password;
}
