package com.locflix.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de um vídeo associado a um filme")
public class VideoResponse {

    @Schema(description = "ID único do vídeo", example = "1")
    private Long id;

    @Schema(description = "ID do filme", example = "1")
    private Long movieId;

    @Schema(description = "Título do filme", example = "Inception")
    private String movieTitle;

    @Schema(description = "Status do processamento", example = "READY")
    private String status;

    @Schema(description = "Descrição legível do status", example = "Pronto")
    private String statusDisplay;

    @Schema(description = "URL do manifest HLS (.m3u8)", example = "/api/v1/videos/stream/1/master.m3u8")
    private String streamUrl;

    @Schema(description = "Duração em segundos", example = "7200")
    private Integer durationSeconds;

    @Schema(description = "Tamanho do arquivo original em bytes", example = "1073741824")
    private Long fileSizeBytes;

    @Schema(description = "Mensagem de erro (se status = ERROR)", example = "ffmpeg não encontrado")
    private String errorMessage;

    @Schema(description = "Data de criação")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização")
    private LocalDateTime updatedAt;
}
