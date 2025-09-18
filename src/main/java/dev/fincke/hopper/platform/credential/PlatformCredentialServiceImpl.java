package dev.fincke.hopper.platform.credential;

import dev.fincke.hopper.platform.credential.dto.PlatformCredentialCreateRequest;
import dev.fincke.hopper.platform.credential.dto.PlatformCredentialResponse;
import dev.fincke.hopper.platform.credential.dto.PlatformCredentialUpdateRequest;
import dev.fincke.hopper.platform.credential.exception.DuplicateCredentialKeyException;
import dev.fincke.hopper.platform.credential.exception.PlatformCredentialNotFoundException;
import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import dev.fincke.hopper.platform.platform.exception.PlatformNotFoundException;
import dev.fincke.hopper.security.encryption.CredentialEncryptionService;
import dev.fincke.hopper.security.encryption.EncryptedCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// Implements platform credential workflows, including encryption-aware helpers
@Service
@Transactional(readOnly = true) // default to read-only transactions for better performance
public class PlatformCredentialServiceImpl implements PlatformCredentialService 
{
    // * Dependencies
    
    private static final Logger logger = LoggerFactory.getLogger(PlatformCredentialServiceImpl.class);
    
    // Spring injects these collaborators so the service can persist and look up credentials
    private final PlatformCredentialRepository credentialRepository;
    private final PlatformRepository platformRepository;
    private final CredentialEncryptionService encryptionService;
    
    // * Constructor
    
    public PlatformCredentialServiceImpl(PlatformCredentialRepository credentialRepository, 
                                       PlatformRepository platformRepository,
                                       CredentialEncryptionService encryptionService) 
    {
        this.credentialRepository = credentialRepository;
        this.platformRepository = platformRepository;
        this.encryptionService = encryptionService;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional // write operation requires full transaction
    public PlatformCredentialResponse createCredential(PlatformCredentialCreateRequest request) 
    {
        // Ensure the parent platform exists before attaching credentials
        Platform platform = platformRepository.findById(request.platformId())
            .orElseThrow(() -> new PlatformNotFoundException(request.platformId()));
        
        // check for duplicate credential key (business rule: credential keys must be unique per platform)
        Optional<PlatformCredential> existingCredential = credentialRepository
            .findByPlatformIdAndCredentialKey(request.platformId(), request.credentialKey());
        
        if (existingCredential.isPresent()) 
        {
            throw new DuplicateCredentialKeyException(request.platformId(), request.credentialKey());
        }
        
        PlatformCredential credential = new PlatformCredential(
            platform,
            request.credentialKey(),
            request.credentialValue(),
            request.isActive()
        );
        
        PlatformCredential savedCredential = credentialRepository.save(credential);
        return PlatformCredentialResponse.from(savedCredential);
    }
    
    @Override
    @Transactional
    public PlatformCredentialResponse updateCredential(UUID id, PlatformCredentialUpdateRequest request) 
    {
        PlatformCredential credential = credentialRepository.findById(id)
            .orElseThrow(() -> new PlatformCredentialNotFoundException(id));
        
        if (!request.hasUpdates()) 
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // Enforce unique keys when renaming so each platform keeps one secret per key label
        if (request.credentialKey() != null && !request.credentialKey().equals(credential.getCredentialKey())) 
        {
            Optional<PlatformCredential> existingCredential = credentialRepository
                .findByPlatformIdAndCredentialKey(credential.getPlatform().getId(), request.credentialKey());
            
            if (existingCredential.isPresent()) 
            {
                throw new DuplicateCredentialKeyException(credential.getPlatform().getId(), request.credentialKey());
            }
            credential.setCredentialKey(request.credentialKey());
        }
        
        if (request.credentialValue() != null) 
        {
            credential.setCredentialValue(request.credentialValue());
        }
        
        if (request.isActive() != null) 
        {
            credential.setActive(request.isActive());
        }
        
        PlatformCredential savedCredential = credentialRepository.save(credential);
        return PlatformCredentialResponse.from(savedCredential);
    }
    
    @Override
    public PlatformCredentialResponse findById(UUID id) 
    {
        PlatformCredential credential = credentialRepository.findById(id)
            .orElseThrow(() -> new PlatformCredentialNotFoundException(id));
        return PlatformCredentialResponse.from(credential);
    }
    
    @Override
    public List<PlatformCredentialResponse> findAll(UUID platformId, Boolean isActive) 
    {
        List<PlatformCredential> credentials;
        
        if (platformId != null && isActive != null) 
        {
            credentials = credentialRepository.findByPlatformIdAndIsActive(platformId, isActive);
        }
        else if (platformId != null) 
        {
            credentials = credentialRepository.findByPlatformId(platformId);
        }
        else if (isActive != null) 
        {
            credentials = credentialRepository.findByIsActive(isActive);
        }
        else 
        {
            credentials = credentialRepository.findAll();
        }
        
        return credentials.stream()
            .map(PlatformCredentialResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteCredential(UUID id) 
    {
        if (!credentialRepository.existsById(id)) 
        {
            throw new PlatformCredentialNotFoundException(id);
        }
        
        credentialRepository.deleteById(id);
    }
    
    // * Query Operations
    
    @Override
    public List<PlatformCredentialResponse> findByPlatformId(UUID platformId) 
    {
        return credentialRepository.findByPlatformId(platformId).stream()
            .map(PlatformCredentialResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PlatformCredentialResponse> findByPlatformIdAndIsActive(UUID platformId, Boolean isActive) 
    {
        return credentialRepository.findByPlatformIdAndIsActive(platformId, isActive).stream()
            .map(PlatformCredentialResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public PlatformCredentialResponse findByPlatformIdAndCredentialKey(UUID platformId, String credentialKey) 
    {
        PlatformCredential credential = credentialRepository
            .findByPlatformIdAndCredentialKey(platformId, credentialKey.trim())
            .orElseThrow(() -> new PlatformCredentialNotFoundException(platformId, credentialKey));
        
        return PlatformCredentialResponse.from(credential);
    }
    
    @Override
    public List<PlatformCredentialResponse> findByPlatformName(String platformName) 
    {
        return credentialRepository.findByPlatformName(platformName.trim()).stream()
            .map(PlatformCredentialResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PlatformCredentialResponse> findByPlatformNameAndIsActive(String platformName, Boolean isActive) 
    {
        return credentialRepository.findByPlatformNameAndIsActive(platformName.trim(), isActive).stream()
            .map(PlatformCredentialResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Secure Credential Operations
    
    @Override
    public String getDecryptedCredentialValue(UUID credentialId) 
    {
        PlatformCredential credential = credentialRepository.findById(credentialId)
            .orElseThrow(() -> new PlatformCredentialNotFoundException(credentialId));
        
        // Skip decryption when legacy records are still stored in plaintext
        if (!credential.isEncrypted()) 
        {
            logger.warn("Credential {} is not encrypted - returning as-is", credentialId);
            return credential.getCredentialValue();
        }
        
        // Rehydrate persisted metadata so the encryption service can verify integrity
        EncryptedCredential encryptedCredential = credential.toEncryptedCredential();
        if (encryptedCredential == null) 
        {
            throw new IllegalStateException("Invalid encryption metadata for credential: " + credentialId);
        }
        
        try 
        {
            String decryptedValue = encryptionService.decrypt(encryptedCredential);
            
            // Capture decryption activity for security audits
            logger.info("Credential decrypted for platform: {}, credentialKey: {}, keyId: {}", 
                       credential.getPlatform().getName(), 
                       credential.getCredentialKey(), 
                       credential.getKeyId());
            
            return decryptedValue;
        } 
        catch (Exception e) 
        {
            logger.error("Failed to decrypt credential {}: {}", credentialId, e.getMessage(), e);
            throw new RuntimeException("Credential decryption failed", e);
        }
    }
    
    @Override
    public boolean validateCredentialEncryption(UUID credentialId) 
    {
        try 
        {
            PlatformCredential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new PlatformCredentialNotFoundException(credentialId));
            
            if (!credential.isEncrypted()) 
            {
                logger.debug("Credential {} is not encrypted", credentialId);
                return false;
            }
            
            EncryptedCredential encryptedCredential = credential.toEncryptedCredential();
            // Delegate validation to encryption service so it can catch drift in key material
            return encryptedCredential != null && encryptionService.validateEncryption(encryptedCredential);
        } 
        catch (Exception e) 
        {
            logger.debug("Encryption validation failed for credential {}: {}", credentialId, e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional
    public PlatformCredentialResponse reEncryptCredential(UUID credentialId) 
    {
        PlatformCredential credential = credentialRepository.findById(credentialId)
            .orElseThrow(() -> new PlatformCredentialNotFoundException(credentialId));
        
        if (!credential.isEncrypted()) 
        {
            throw new IllegalArgumentException("Credential is not encrypted: " + credentialId);
        }
        
        EncryptedCredential oldEncrypted = credential.toEncryptedCredential();
        if (oldEncrypted == null) 
        {
            throw new IllegalStateException("Invalid encryption metadata for credential: " + credentialId);
        }
        
        try 
        {
            // Run decryption + encryption cycle so data matches the latest policy
            EncryptedCredential newEncrypted = encryptionService.reEncrypt(oldEncrypted);
            
            // Persist metadata returned by the encryption service to keep entity consistent
            credential.updateFromEncryptedCredential(newEncrypted);
            
            PlatformCredential savedCredential = credentialRepository.save(credential);
            
            logger.info("Credential re-encrypted: {} from version {} to {}", 
                       credentialId, oldEncrypted.encryptionVersion(), newEncrypted.encryptionVersion());
            
            return PlatformCredentialResponse.from(savedCredential);
        } 
        catch (Exception e) 
        {
            logger.error("Failed to re-encrypt credential {}: {}", credentialId, e.getMessage(), e);
            throw new RuntimeException("Credential re-encryption failed", e);
        }
    }
    
    @Override
    public List<PlatformCredentialResponse> findCredentialsNeedingReEncryption() 
    {
        // Surface rotation backlog to API clients instead of relying on ad-hoc SQL
        List<PlatformCredential> allCredentials = credentialRepository.findAll();
        
        return allCredentials.stream()
            .filter(PlatformCredential::isEncrypted)
            .filter(credential -> {
                EncryptedCredential encrypted = credential.toEncryptedCredential();
                return encrypted != null && encryptionService.needsReEncryption(encrypted);
            })
            .map(PlatformCredentialResponse::from)
            .collect(Collectors.toList());
    }
}
