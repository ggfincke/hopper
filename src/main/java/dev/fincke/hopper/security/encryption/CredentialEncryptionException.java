package dev.fincke.hopper.security.encryption;

// Base type for surfacing encryption system failures up through service layers
public class CredentialEncryptionException extends RuntimeException 
{
    // * Constructors

    // Standard exception with error message only
    public CredentialEncryptionException(String message) 
    {
        super(message);
    }

    // Exception with error message and underlying cause
    public CredentialEncryptionException(String message, Throwable cause) 
    {
        super(message, cause);
    }
}
