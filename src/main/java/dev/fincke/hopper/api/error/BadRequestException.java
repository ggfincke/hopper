package dev.fincke.hopper.api.error;

/**
 * Base exception for domain exceptions that should result in 400 BAD_REQUEST responses.
 * <p>
 * Extend this class on domain exceptions for invalid input, validation failures,
 * or illegal state transitions to automatically map them to HTTP 400 status codes in
 * the global exception handler.
 */
public abstract class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
