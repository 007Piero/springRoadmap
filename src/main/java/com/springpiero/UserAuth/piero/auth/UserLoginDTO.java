package com.springpiero.UserAuth.piero.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {

    @Email
    @Schema(description = "Email address", example = "admin@medael.ca", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(min = 6, max = 20)
    @Schema(description = "Password", example = "123456",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20, minLength = 6)
    private String password;

}
