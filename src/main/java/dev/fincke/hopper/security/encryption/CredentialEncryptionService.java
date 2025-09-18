package dev.fincke.hopper.security.encryption;

// Abstraction that keeps the rest of the application unaware of how credentials are encrypted
public interface CredentialEncryptionService 
{
    // * Core Encryption Operations
    
    // Seal plaintext with the active algorithm and return metadata required for decryption
    EncryptedCredential encrypt(String plaintext);
    
    // Recover plaintext when an operator or integration explicitly requests it
    String decrypt(EncryptedCredential encryptedCredential);
    
    // Rotate ciphertext so older records benefit from updated algorithms or keys
    EncryptedCredential reEncrypt(EncryptedCredential oldEncryptedCredential);
    
    // * Validation Methods
    
    // Confirm that stored metadata and master key are still aligned
    boolean validateEncryption(EncryptedCredential encryptedCredential);
    
    // Indicate whether policy changes or age thresholds require re-encryption
    boolean needsReEncryption(EncryptedCredential encryptedCredential);
    
    // * Utility Methods
    
    // Expose the algorithm identifier for audit logging and API clients
    String getCurrentEncryptionVersion();
    
    // Let callers short-circuit when master key material is missing or invalid
    boolean isMasterKeyConfigured();
}
