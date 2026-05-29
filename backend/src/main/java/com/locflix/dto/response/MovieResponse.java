package com.locflix.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para resposta com dados de um Filme.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de um filme do catálogo")
public class MovieResponse {

    @Schema(description = "ID único do filme", example = "1")
    private Long id;

    @Schema(description = "Título do filme", example = "Inception")
    private String title;

    @Schema(description = "Descrição do filme", example = "Um ladrão que furta segredos corporativos...")
    private String description;

    @Schema(description = "Gênero do filme", example = "Ficção Científica")
    private String genre;

    @Schema(description = "Diretor do filme", example = "Christopher Nolan")
    private String director;

    @Schema(description = "Tipo de conteúdo", example = "movie")
    private String movieType;

    @Schema(description = "Data de lançamento", example = "2010-07-16")
    private LocalDate releaseDate;

    @Schema(description = "Duração em minutos", example = "148")
    private Integer durationMinutes;

    @Schema(description = "Preço de locação", example = "14.99")
    private BigDecimal price;

    @Schema(description = "URL da imagem", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Disponibilidade do filme", example = "true")
    private Boolean available;

    @Schema(description = "Categorias do filme", example = "[\"Ficção Científica\", \"Ação\"]")
    private Set<String> categories;

    @Schema(description = "Data de criação", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2024-01-05T15:30:00")
    private LocalDateTime updatedAt;
}

