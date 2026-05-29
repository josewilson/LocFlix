package com.locflix.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta com dados de uma Locação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de uma locação de filme")
public class RentalResponse {

    @Schema(description = "ID único da locação", example = "1")
    private Long id;

    @Schema(description = "ID do usuário", example = "1")
    private Long userId;

    @Schema(description = "Nome do usuário", example = "João Silva")
    private String userName;

    @Schema(description = "ID do filme", example = "1")
    private Long movieId;

    @Schema(description = "Título do filme", example = "Inception")
    private String movieTitle;

    @Schema(description = "Data e hora do aluguel", example = "2024-01-01T10:00:00")
    private LocalDateTime rentalDate;

    @Schema(description = "Data e hora de devolução esperada", example = "2024-01-08T10:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Data e hora da devolução (se devolvido)", example = "2024-01-08T09:30:00")
    private LocalDateTime returnDate;

    @Schema(description = "Preço total da locação", example = "104.93")
    private BigDecimal totalPrice;

    @Schema(description = "Status da locação (ACTIVE, COMPLETED, OVERDUE, CANCELLED)", example = "ACTIVE")
    private String status;

    @Schema(description = "Se a locação está atrasada", example = "false")
    private Boolean isOverdue;

    @Schema(description = "Dias da locação", example = "7")
    private Long daysRented;

    @Schema(description = "Data de criação", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2024-01-08T10:00:00")
    private LocalDateTime updatedAt;
}

