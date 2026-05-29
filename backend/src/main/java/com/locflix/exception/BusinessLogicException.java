package com.locflix.exception;

/**
 * Exceção lançada quando há violação de regra de negócio.
 */
public class BusinessLogicException extends RuntimeException {

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}

