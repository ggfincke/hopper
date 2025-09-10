package dev.fincke.hopper.platform.credential;

import dev.fincke.hopper.platform.credential.dto.PlatformCredentialCreateRequest;
import dev.fincke.hopper.platform.credential.dto.PlatformCredentialResponse;
import dev.fincke.hopper.platform.credential.dto.PlatformCredentialUpdateRequest;
import dev.fincke.hopper.platform.credential.exception.DuplicateCredentialKeyException;
import dev.fincke.hopper.platform.credential.exception.PlatformCredentialNotFoundException;
import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import dev.fincke.hopper.platform.platform.exception.PlatformNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// Service implementation for platform credential business operations
@Service
@Transactional(readOnly = true) // default to read-only transactions for better performance
public class PlatformCredentialServiceImpl implements PlatformCredentialService 
{
    // * Dependencies
    
    // Spring will inject repository dependencies
    private final PlatformCredentialRepository credentialRepository;
    private final PlatformRepository platformRepository;
    
    // * Constructor
    
    public PlatformCredentialServiceImpl(PlatformCredentialRepository credentialRepository, 
                                       PlatformRepository platformRepository) 
    {
        this.credentialRepository = credentialRepository;
        this.platformRepository = platformRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional // write operation requires full transaction
    public PlatformCredentialResponse createCredential(PlatformCredentialCreateRequest request) 
    {
        // validate platform exists
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
        
        // update credential key if changed (check for duplicate)
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
            credential.setIsActive(request.isActive());
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
}