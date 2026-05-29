package com.locflix.exception;

/**
 * Exceção lançada quando há erro de autenticação JWT.
 */
public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException(String message) {
        super(message);
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

