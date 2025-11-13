package dev.fincke.hopper.api.error;

/**
 * Base exception for domain exceptions that should result in 409 CONFLICT responses.
 * <p>
 * Extend this class on domain exceptions for duplicate resources, constraint
 * violations, or deletion restrictions to automatically map them to HTTP 409 status
 * codes in the global exception handler.
 */
public abstract class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
