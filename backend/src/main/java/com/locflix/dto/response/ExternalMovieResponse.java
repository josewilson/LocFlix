package com.locflix.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Dados vindos de uma API externa de catálogo de filmes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de um filme obtido de uma API externa")
public class ExternalMovieResponse {

    @Schema(description = "Identificador externo do filme", example = "tt1375666")
    private String externalId;

    @Schema(description = "Título do filme", example = "Inception")
    private String title;

    @Schema(description = "Sinopse ou descrição", example = "A thief who steals corporate secrets...")
    private String description;

    @Schema(description = "Gênero principal", example = "Sci-Fi")
    private String genre;

    @Schema(description = "Diretor", example = "Christopher Nolan")
    private String director;

    @Schema(description = "Tipo do conteúdo", example = "movie")
    private String movieType;

    @Schema(description = "Data de lançamento", example = "2010-07-16")
    private LocalDate releaseDate;

    @Schema(description = "Ano de lançamento", example = "2010")
    private String releaseYear;

    @Schema(description = "Duração em minutos", example = "148")
    private Integer durationMinutes;

    @Schema(description = "URL da capa/poster", example = "https://image.tmdb.org/t/p/w500/...jpg")
    private String imageUrl;
}

