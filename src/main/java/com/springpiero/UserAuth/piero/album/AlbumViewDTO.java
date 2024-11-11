package com.springpiero.UserAuth.piero.album;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlbumViewDTO {

    private Long id;

    @NotBlank
    @Schema(description = "Album name", example = "Amour vrai", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    private String description;
}
