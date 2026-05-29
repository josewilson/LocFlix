package com.locflix.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta com dados de uma Categoria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de uma categoria de filme")
public class CategoryResponse {

    @Schema(description = "ID único da categoria", example = "1")
    private Long id;

    @Schema(description = "Nome da categoria", example = "Ficção Científica")
    private String name;

    @Schema(description = "Descrição da categoria", example = "Filmes de ficção científica e futurismo")
    private String description;

    @Schema(description = "Data de criação", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2024-01-05T15:30:00")
    private LocalDateTime updatedAt;
}

