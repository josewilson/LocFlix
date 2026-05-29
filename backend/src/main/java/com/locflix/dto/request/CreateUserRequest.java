package com.locflix.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de registro de novo usuário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para registro de novo usuário")
public class CreateUserRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255, message = "Email não pode ter mais de 255 caracteres")
    @Schema(description = "Email do usuário (deve ser único)", example = "newuser@example.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 50, message = "Senha deve ter entre 8 e 50 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Senha deve conter letras maiúsculas, minúsculas, números e caracteres especiais"
    )
    @Schema(
            description = "Senha (mín 8 caracteres com maiúsculas, minúsculas, números e caracteres especiais)",
            example = "SecurePass123!"
    )
    private String password;

    @NotBlank(message = "Primeiro nome é obrigatório")
    @Size(min = 2, max = 100, message = "Primeiro nome deve ter entre 2 e 100 caracteres")
    @Schema(description = "Primeiro nome do usuário", example = "João")
    private String firstName;

    @Size(max = 100, message = "Sobrenome não pode ter mais de 100 caracteres")
    @Schema(description = "Sobrenome do usuário (opcional)", example = "Silva")
    private String lastName;
}

