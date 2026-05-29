package com.locflix.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de criação ou atualização de uma Categoria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar ou atualizar uma categoria")
public class CreateCategoryRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 1, max = 100, message = "Nome deve ter entre 1 e 100 caracteres")
    @Schema(description = "Nome da categoria", example = "Ficção Científica")
    private String name;

    @Size(max = 500, message = "Descrição não pode ter mais de 500 caracteres")
    @Schema(description = "Descrição da categoria", example = "Filmes de ficção científica e futurismo")
    private String description;
}

