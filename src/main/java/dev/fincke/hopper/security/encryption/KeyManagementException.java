package dev.fincke.hopper.security.encryption;

// Differentiates failures related to master key handling from general encryption errors
public class KeyManagementException extends CredentialEncryptionException 
{
    // * Constructors

    // Standard exception with error message only
    public KeyManagementException(String message) 
    {
        super(message);
    }

    // Exception with error message and underlying cause
    public KeyManagementException(String message, Throwable cause) 
    {
        super(message, cause);
    }
}
