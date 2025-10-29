package dev.fincke.hopper.api.error;

/**
 * Base exception for domain exceptions that should result in 500 INTERNAL_SERVER_ERROR responses.
 * <p>
 * Extend this class on domain exceptions for critical system failures like
 * encryption/decryption errors or key management issues that require immediate attention.
 */
public abstract class ServerErrorException extends RuntimeException {
    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
