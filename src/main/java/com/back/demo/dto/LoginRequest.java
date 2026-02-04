package com.back.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Credenciales de login")
public class LoginRequest {

    @NotBlank(message = "El usuario es obligatorio")
    @Schema(description = "Nombre de usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
