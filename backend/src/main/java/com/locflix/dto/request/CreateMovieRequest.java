package com.locflix.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para requisição de criação ou atualização de um Filme.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar ou atualizar um filme")
public class CreateMovieRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 1, max = 255, message = "Título deve ter entre 1 e 255 caracteres")
    @Schema(description = "Título do filme", example = "Inception")
    private String title;

    @Size(max = 5000, message = "Descrição não pode ter mais de 5000 caracteres")
    @Schema(description = "Descrição detalhada do filme", example = "Um ladrão que furta segredos corporativos...")
    private String description;

    @Size(max = 100, message = "Gênero não pode ter mais de 100 caracteres")
    @Schema(description = "Gênero do filme", example = "Ficção Científica")
    private String genre;

    @Size(max = 255, message = "Diretor não pode ter mais de 255 caracteres")
    @Schema(description = "Diretor do filme", example = "Christopher Nolan")
    private String director;

    @Size(max = 50, message = "Tipo não pode ter mais de 50 caracteres")
    @Schema(description = "Tipo do conteúdo", example = "movie")
    private String movieType;

    @PastOrPresent(message = "Data de lançamento não pode ser no futuro")
    @Schema(description = "Data de lançamento", example = "2010-07-16")
    private LocalDate releaseDate;

    @Positive(message = "Duração deve ser um número positivo")
    @Schema(description = "Duração em minutos", example = "148")
    private Integer durationMinutes;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @DecimalMax(value = "999.99", message = "Preço não pode ser maior que 999.99")
    @Schema(description = "Preço da locação", example = "14.99")
    private BigDecimal price;

    @Pattern(
            regexp = "^(http|https)://.*",
            message = "Deve ser uma URL válida começando com http:// ou https://"
    )
    @Schema(description = "URL da imagem do filme", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Se o filme está disponível para locação", example = "true")
    private Boolean available = true;
}

