package com.back.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de login con token JWT")
public class LoginResponse {

    @Schema(description = "Token JWT para autorización")
    private String token;

    @Schema(description = "Tipo del token")
    private String type;

    @Schema(description = "Nombre de usuario")
    private String username;
}
