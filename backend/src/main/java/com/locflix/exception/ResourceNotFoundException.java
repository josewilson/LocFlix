package com.locflix.exception;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceNotFoundException of(String resourceName, String fieldName, Object fieldValue) {
        return new ResourceNotFoundException(
                String.format("%s não encontrado com %s: '%s'", resourceName, fieldName, fieldValue)
        );
    }
}

