package com.locflix.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login (token JWT).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta de autenticação com token JWT")
public class LoginResponse {

    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Tempo de expiração em milissegundos", example = "86400000")
    private Long expiresIn;

    @Schema(description = "Dados do usuário autenticado")
    private UserResponse user;
}

