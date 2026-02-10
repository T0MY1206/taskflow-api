package com.back.demo.controller;

import com.back.demo.dto.LoginRequest;
import com.back.demo.dto.LoginResponse;
import com.back.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación: login con usuario/contraseña y obtención de token JWT para el resto de la API")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Login",
            description = "Autentica con usuario y contraseña. Devuelve un token JWT que debe enviarse en el header 'Authorization: Bearer <token>' en las peticiones a /api/v1/tasks. Usuarios de prueba: user/user123, admin/admin123."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(value = "{\"token\":\"eyJhbGc...\",\"type\":\"Bearer\",\"username\":\"user\"}"))),
            @ApiResponse(responseCode = "400", description = "Body inválido (falta username o password)"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
