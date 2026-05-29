package com.locflix.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Classe padrão para respostas de erro da API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Timestamp da ocorrência do erro
     */
    private LocalDateTime timestamp;

    /**
     * Status HTTP
     */
    private int status;

    /**
     * Descrição do status HTTP
     */
    private String statusDescription;

    /**
     * Mensagem de erro
     */
    private String message;

    /**
     * Erros de validação de campos
     */
    private Map<String, String> fieldErrors;

    /**
     * Lista de mensagens de erro
     */
    private List<String> errors;

    /**
     * Caminho da requisição
     */
    private String path;

    /**
     * Cria um ErrorResponse a partir dos parâmetros básicos.
     */
    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .statusDescription(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Cria um ErrorResponse com erros de campos.
     */
    public static ErrorResponse of(HttpStatus status, String message, String path, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .statusDescription(status.getReasonPhrase())
                .message(message)
                .fieldErrors(fieldErrors)
                .path(path)
                .build();
    }
}

