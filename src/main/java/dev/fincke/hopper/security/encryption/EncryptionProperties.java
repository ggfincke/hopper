package dev.fincke.hopper.security.encryption;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Binds credential encryption settings so behavior can be tuned without recompiling
@Component
@ConfigurationProperties(prefix = "app.encryption")
public class EncryptionProperties 
{
    // * Attributes
    
    // Local fallback for the master key; production overrides via environment variable
    private String masterKey = "";
    
    // Identifier persisted with ciphertext so we can detect when rotation is needed
    private String encryptionVersion = "AES-GCM-256-V1";
    
    // PBKDF2 iteration count balances brute-force resistance with runtime costs
    private int keyDerivationIterations = 100000;
    
    // Configure salt length to match the desired entropy budget
    private int saltLength = 32;
    
    // Authentication tag size controls how resistant the ciphertext is to forgery
    private int gcmTagLength = 128;
    
    // Recommended rotation interval in days (0 disables time-based rotation)
    private int keyRotationDays = 90;
    
    // Toggle audit logging to balance observability with log volume
    private boolean auditLogging = true;
    
    // Age threshold that triggers re-encryption heuristics
    private int maxCredentialAge = 365;

    // * Getters and Setters

    public String getMasterKey() 
    {
        return masterKey;
    }

    public void setMasterKey(String masterKey) 
    {
        this.masterKey = masterKey;
    }

    public String getEncryptionVersion() 
    {
        return encryptionVersion;
    }

    public void setEncryptionVersion(String encryptionVersion) 
    {
        this.encryptionVersion = encryptionVersion;
    }

    public int getKeyDerivationIterations() 
    {
        return keyDerivationIterations;
    }

    public void setKeyDerivationIterations(int keyDerivationIterations) 
    {
        this.keyDerivationIterations = keyDerivationIterations;
    }

    public int getSaltLength() 
    {
        return saltLength;
    }

    public void setSaltLength(int saltLength) 
    {
        this.saltLength = saltLength;
    }

    public int getGcmTagLength() 
    {
        return gcmTagLength;
    }

    public void setGcmTagLength(int gcmTagLength) 
    {
        this.gcmTagLength = gcmTagLength;
    }

    public int getKeyRotationDays() 
    {
        return keyRotationDays;
    }

    public void setKeyRotationDays(int keyRotationDays) 
    {
        this.keyRotationDays = keyRotationDays;
    }

    public boolean isAuditLogging() 
    {
        return auditLogging;
    }

    public void setAuditLogging(boolean auditLogging) 
    {
        this.auditLogging = auditLogging;
    }

    public int getMaxCredentialAge() 
    {
        return maxCredentialAge;
    }

    public void setMaxCredentialAge(int maxCredentialAge) 
    {
        this.maxCredentialAge = maxCredentialAge;
    }

    // * Validation Methods

    // Guard helper for callers who must refuse to work without a strong key
    public boolean isValidMasterKey() 
    {
        return masterKey != null && !masterKey.trim().isEmpty() && masterKey.length() >= 32;
    }
    
    // Prefer environment override so production never relies on baked-in defaults
    public String getEffectiveMasterKey() 
    {
        String envKey = System.getenv("CREDENTIAL_MASTER_KEY");
        return envKey != null && !envKey.trim().isEmpty() ? envKey : masterKey;
    }
}
