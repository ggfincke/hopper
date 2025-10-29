package dev.fincke.hopper.api.error;

/**
 * Base exception for domain exceptions that should result in 404 NOT_FOUND responses.
 * <p>
 * Extend this class on domain exceptions to automatically map them to HTTP 404
 * status codes in the global exception handler, eliminating the need to explicitly
 * register each exception class.
 */
public abstract class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
