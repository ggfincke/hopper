package dev.fincke.hopper.security.encryption;

import java.time.LocalDateTime;
import java.util.Base64;

// Value object that carries encrypted credential metadata required for rotation and decryption
public record EncryptedCredential(
    // Ciphertext stored as Base64 so it fits inside character columns
    String encryptedValue,
    
    // Persisted salt lets us rebuild the derived key during decrypt
    String salt,
    
    // Algorithm identifier so we know which policy produced this ciphertext
    String encryptionVersion,
    
    // Timestamp used to decide when rotation should run again
    LocalDateTime encryptedAt,
    
    // Unique id ties the ciphertext to the derived key version used
    String keyId
) 
{
    // * Static Factory Methods
    
    // Factory used immediately after encryption; sets current timestamp automatically
    public static EncryptedCredential of(byte[] encryptedBytes, byte[] saltBytes, 
                                       String encryptionVersion, String keyId) 
    {
        return new EncryptedCredential(
            Base64.getEncoder().encodeToString(encryptedBytes),
            Base64.getEncoder().encodeToString(saltBytes),
            encryptionVersion,
            LocalDateTime.now(),
            keyId
        );
    }
    
    // Factory for reconstructing the value object from persisted columns
    public static EncryptedCredential fromDatabase(String encryptedValue, String salt, 
                                                 String encryptionVersion, LocalDateTime encryptedAt, 
                                                 String keyId) 
    {
        return new EncryptedCredential(encryptedValue, salt, encryptionVersion, encryptedAt, keyId);
    }
    
    // * Utility Methods
    
    // Decode ciphertext for cryptographic operations
    public byte[] getEncryptedBytes() 
    {
        return Base64.getDecoder().decode(encryptedValue);
    }
    
    // Decode salt so PBKDF2 can rebuild the derived key
    public byte[] getSaltBytes() 
    {
        return Base64.getDecoder().decode(salt);
    }
    
    // Determine if ciphertext already matches the system's current algorithm identifier
    public boolean isCurrentVersion(String currentVersion) 
    {
        return encryptionVersion != null && encryptionVersion.equals(currentVersion);
    }
    
    // Calculate credential age to support rotation policies based on time
    public long getAgeInDays() 
    {
        return java.time.temporal.ChronoUnit.DAYS.between(encryptedAt, LocalDateTime.now());
    }
    
    // Helper to ask whether rotation windows based on age have expired
    public boolean isOlderThan(int days) 
    {
        return getAgeInDays() > days;
    }
    
    // * Validation
    
    // Ensure the record has the minimum metadata needed for safe decrypt operations
    public boolean isValid() 
    {
        return encryptedValue != null && !encryptedValue.trim().isEmpty() &&
               salt != null && !salt.trim().isEmpty() &&
               encryptionVersion != null && !encryptionVersion.trim().isEmpty() &&
               encryptedAt != null &&
               keyId != null && !keyId.trim().isEmpty();
    }
}
