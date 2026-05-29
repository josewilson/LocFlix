package com.locflix.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de criação de uma Locação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criar uma locação de filme")
public class CreateRentalRequest {

    @NotNull(message = "ID do filme é obrigatório")
    @Schema(description = "ID do filme a alugar", example = "1")
    private Long movieId;

    @NotNull(message = "Quantidade de dias é obrigatória")
    @Min(value = 1, message = "Quantidade de dias deve ser no mínimo 1")
    @Schema(description = "Número de dias para alugar", example = "7")
    private Integer daysToRent;
}

