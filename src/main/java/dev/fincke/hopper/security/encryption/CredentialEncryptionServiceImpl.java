package dev.fincke.hopper.security.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.UUID;

// AES-256-GCM implementation of credential encryption that satisfies the service contract
@Service
public class CredentialEncryptionServiceImpl implements CredentialEncryptionService 
{
    // * Attributes
    
    private static final Logger logger = LoggerFactory.getLogger(CredentialEncryptionServiceImpl.class);
    
    // Centralize algorithm choices so rotation can be reasoned about in one place
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int AES_KEY_LENGTH = 256;
    private static final int IV_LENGTH = 12; // 96-bit IV recommended for secure GCM usage
    
    // Injected collaborators provide configuration and entropy
    private final EncryptionProperties encryptionProperties;
    private final SecureRandom secureRandom;
    
    // * Constructor
    
    public CredentialEncryptionServiceImpl(EncryptionProperties encryptionProperties) 
    {
        this.encryptionProperties = encryptionProperties;
        this.secureRandom = new SecureRandom();
        
        // Fail fast in logs if the required master key is missing
        if (!isMasterKeyConfigured()) 
        {
            logger.warn("Master key not properly configured. Credential encryption will not work.");
        }
    }
    
    // * Core Encryption Operations
    
    @Override
    public EncryptedCredential encrypt(String plaintext) 
    {
        if (plaintext == null || plaintext.isEmpty()) 
        {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        
        if (!isMasterKeyConfigured()) 
        {
            throw new KeyManagementException("Master key not configured for encryption");
        }
        
        try 
        {
            // Use a per-credential salt so identical secrets never share key material
            byte[] salt = generateSalt();
            
            // Derive a symmetric key from the master key plus salt
            SecretKey encryptionKey = deriveKey(salt);
            
            // Fresh IV keeps AES-GCM semantic security guarantees intact
            byte[] iv = generateIV();
            
            // Run AES-GCM with the derived key so data remains confidential and tamper-evident
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(encryptionProperties.getGcmTagLength(), iv);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, gcmSpec);
            
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = cipher.doFinal(plaintextBytes);
            
            // Persist IV alongside ciphertext so decryption can rebuild the state
            byte[] combined = new byte[IV_LENGTH + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encryptedBytes, 0, combined, IV_LENGTH, encryptedBytes.length);
            
            // Tag ciphertext with an identifier to support key rotation audits
            String keyId = generateKeyId();
            
            // Emit audit trail when policy requires it
            if (encryptionProperties.isAuditLogging()) 
            {
                logger.info("Credential encrypted with version: {}, keyId: {}", 
                          encryptionProperties.getEncryptionVersion(), keyId);
            }
            
            return EncryptedCredential.of(combined, salt, encryptionProperties.getEncryptionVersion(), keyId);
        } 
        catch (Exception e) 
        {
            logger.error("Failed to encrypt credential", e);
            throw new CredentialEncryptionException("Encryption failed", e);
        }
    }
    
    @Override
    public String decrypt(EncryptedCredential encryptedCredential) 
    {
        if (encryptedCredential == null || !encryptedCredential.isValid()) 
        {
            throw new IllegalArgumentException("Invalid encrypted credential");
        }
        
        if (!isMasterKeyConfigured()) 
        {
            throw new KeyManagementException("Master key not configured for decryption");
        }
        
        try 
        {
            // Recreate the symmetric key using the stored salt plus master key
            SecretKey decryptionKey = deriveKey(encryptedCredential.getSaltBytes());
            
            // Separate the prepended IV so AES-GCM can be initialized correctly
            byte[] combined = encryptedCredential.getEncryptedBytes();
            if (combined.length <= IV_LENGTH) 
            {
                throw new CredentialDecryptionException("Invalid encrypted data length");
            }
            
            byte[] iv = Arrays.copyOfRange(combined, 0, IV_LENGTH);
            byte[] encryptedData = Arrays.copyOfRange(combined, IV_LENGTH, combined.length);
            
            // Decrypt and verify authenticity in one step
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(encryptionProperties.getGcmTagLength(), iv);
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey, gcmSpec);
            
            byte[] decryptedBytes = cipher.doFinal(encryptedData);
            
            // Emit audit trail when policy requires it
            if (encryptionProperties.isAuditLogging()) 
            {
                logger.info("Credential decrypted with version: {}, keyId: {}", 
                          encryptedCredential.encryptionVersion(), encryptedCredential.keyId());
            }
            
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } 
        catch (Exception e) 
        {
            logger.error("Failed to decrypt credential with keyId: {}", encryptedCredential.keyId(), e);
            throw new CredentialDecryptionException("Decryption failed", e);
        }
    }
    
    @Override
    public EncryptedCredential reEncrypt(EncryptedCredential oldEncryptedCredential) 
    {
        // Rely on existing metadata to unwrap, then reseal with the latest configuration
        String plaintext = decrypt(oldEncryptedCredential);
        EncryptedCredential newEncrypted = encrypt(plaintext);
        
        logger.info("Credential re-encrypted from version {} to {}", 
                   oldEncryptedCredential.encryptionVersion(), newEncrypted.encryptionVersion());
        
        return newEncrypted;
    }
    
    // * Validation Methods
    
    @Override
    public boolean validateEncryption(EncryptedCredential encryptedCredential) 
    {
        if (encryptedCredential == null || !encryptedCredential.isValid()) 
        {
            return false;
        }
        
        try 
        {
            // A full decrypt round-trip confirms the metadata still matches the master key
            decrypt(encryptedCredential);
            return true;
        } 
        catch (Exception e) 
        {
            logger.debug("Encryption validation failed for keyId: {}", encryptedCredential.keyId(), e);
            return false;
        }
    }
    
    @Override
    public boolean needsReEncryption(EncryptedCredential encryptedCredential) 
    {
        if (encryptedCredential == null) 
        {
            return false;
        }
        
        // Upgrade credentials sealed under previous algorithm identifiers
        if (!encryptedCredential.isCurrentVersion(encryptionProperties.getEncryptionVersion())) 
        {
            return true;
        }
        
        // Consider age to force rotation even when version remains unchanged
        if (encryptedCredential.isOlderThan(encryptionProperties.getMaxCredentialAge())) 
        {
            return true;
        }
        
        return false;
    }
    
    // * Utility Methods
    
    @Override
    public String getCurrentEncryptionVersion() 
    {
        return encryptionProperties.getEncryptionVersion();
    }
    
    @Override
    public boolean isMasterKeyConfigured() 
    {
        String masterKey = encryptionProperties.getEffectiveMasterKey();
        return masterKey != null && !masterKey.trim().isEmpty() && masterKey.length() >= 32;
    }
    
    // * Private Helper Methods
    
    // Provide entropy so each credential derives a distinct encryption key
    private byte[] generateSalt() 
    {
        byte[] salt = new byte[encryptionProperties.getSaltLength()];
        secureRandom.nextBytes(salt);
        return salt;
    }
    
    // Supply the required per-operation IV for AES-GCM
    private byte[] generateIV() 
    {
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }
    
    // Use PBKDF2 so brute-force attempts against the master key are slowed dramatically
    private SecretKey deriveKey(byte[] salt) throws Exception 
    {
        String masterKey = encryptionProperties.getEffectiveMasterKey();
        
        KeySpec spec = new PBEKeySpec(
            masterKey.toCharArray(), 
            salt, 
            encryptionProperties.getKeyDerivationIterations(), 
            AES_KEY_LENGTH
        );
        
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    
    // Key identifier lets us correlate stored ciphertext with rotation events
    private String generateKeyId() 
    {
        return UUID.randomUUID().toString();
    }
}
