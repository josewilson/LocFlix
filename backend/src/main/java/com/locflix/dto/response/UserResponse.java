package com.locflix.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para resposta de dados de usuário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de um usuário")
public class UserResponse {

    @Schema(description = "ID único do usuário", example = "1")
    private Long id;

    @Schema(description = "Email do usuário", example = "user@example.com")
    private String email;

    @Schema(description = "Primeiro nome", example = "João")
    private String firstName;

    @Schema(description = "Sobrenome", example = "Silva")
    private String lastName;

    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String fullName;

    @Schema(description = "Status de ativação da conta", example = "true")
    private Boolean active;

    @Schema(description = "Lista de papéis (roles) do usuário", example = "[\"USER\", \"ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "Data e hora de criação", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data e hora da última atualização", example = "2024-01-05T15:30:00")
    private LocalDateTime updatedAt;
}

