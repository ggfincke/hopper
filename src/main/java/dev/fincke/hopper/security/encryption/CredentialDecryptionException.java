package dev.fincke.hopper.security.encryption;

// Signals that cipher text could not be decrypted so callers can react differently from encryption errors
public class CredentialDecryptionException extends CredentialEncryptionException 
{
    // * Constructors

    // Standard exception with error message only
    public CredentialDecryptionException(String message) 
    {
        super(message);
    }

    // Exception with error message and underlying cause
    public CredentialDecryptionException(String message, Throwable cause) 
    {
        super(message, cause);
    }
}
