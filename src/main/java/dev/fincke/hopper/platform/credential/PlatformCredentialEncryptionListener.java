package dev.fincke.hopper.platform.credential;

import dev.fincke.hopper.security.encryption.CredentialEncryptionService;
import dev.fincke.hopper.security.encryption.EncryptedCredential;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Centralizes credential encryption so JPA writes never persist plaintext secrets
@Component
public class PlatformCredentialEncryptionListener 
{
    // * Attributes
    
    private static final Logger logger = LoggerFactory.getLogger(PlatformCredentialEncryptionListener.class);
    
    // Static reference lets Hibernate-created instances reach the Spring-managed service
    private static CredentialEncryptionService encryptionService;

    @Autowired
    public void setEncryptionService(CredentialEncryptionService encryptionService)
    {
        PlatformCredentialEncryptionListener.encryptionService = encryptionService;
    }
    
    // * JPA Lifecycle Hooks
    
    // Guarantee new credentials are sealed before we write them to the database
    @PrePersist
    public void prePersist(PlatformCredential credential) 
    {
        encryptCredentialValue(credential, "CREATE");
    }
    
    // Re-seal credentials on update in case the secret or encryption policy changed
    @PreUpdate
    public void preUpdate(PlatformCredential credential) 
    {
        // Avoid double-encryption by checking for existing metadata
        if (!credential.isEncrypted()) 
        {
            encryptCredentialValue(credential, "UPDATE");
        }
    }
    
    // Lifecycle hook exists only for traceability; decryption happens via service methods
    @PostLoad
    public void postLoad(PlatformCredential credential) 
    {
        // Note: Automatic decryption on load is disabled for security
        // Decryption only happens when explicitly requested through service methods
        logger.debug("Loaded encrypted credential with keyId: {}", credential.getKeyId());
    }
    
    // * Private Helper Methods
    
    // Shared encryption workflow invoked by JPA hooks
    private void encryptCredentialValue(PlatformCredential credential, String operation) 
    {
        CredentialEncryptionService service = encryptionService;

        if (service == null) 
        {
            logger.warn("Encryption service not available - credential will be stored as plaintext");
            return;
        }
        
        if (!service.isMasterKeyConfigured()) 
        {
            logger.warn("Master key not configured - credential will be stored as plaintext");
            return;
        }
        
        String plaintextValue = credential.getCredentialValue();
        if (plaintextValue == null || plaintextValue.isEmpty()) 
        {
            logger.debug("Empty credential value - skipping encryption");
            return;
        }
        
        try 
        {
            // Protect the plaintext value using the active encryption policy
            EncryptedCredential encrypted = service.encrypt(plaintextValue);
            
            // Copy metadata back so downstream code knows how to decrypt it later
            credential.updateFromEncryptedCredential(encrypted);
            
            logger.info("Credential encrypted on {} with version: {}, keyId: {}", 
                       operation, encrypted.encryptionVersion(), encrypted.keyId());
        } 
        catch (Exception e) 
        {
            logger.error("Failed to encrypt credential on {}: {}", operation, e.getMessage(), e);
            // Logging keeps local development unblocked; production should abort to avoid plaintext storage
        }
    }
}
