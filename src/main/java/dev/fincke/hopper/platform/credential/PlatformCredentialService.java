package dev.fincke.hopper.platform.credential;

import dev.fincke.hopper.platform.credential.dto.PlatformCredentialCreateRequest;
import dev.fincke.hopper.platform.credential.dto.PlatformCredentialResponse;
import dev.fincke.hopper.platform.credential.dto.PlatformCredentialUpdateRequest;

import java.util.List;
import java.util.UUID;

// Service interface for platform credential business operations (separates business rules from data access)
public interface PlatformCredentialService 
{
    // * Core CRUD Operations
    
    // create new platform credential
    PlatformCredentialResponse createCredential(PlatformCredentialCreateRequest request);
    
    // update existing platform credential
    PlatformCredentialResponse updateCredential(UUID id, PlatformCredentialUpdateRequest request);
    
    // find credential by ID
    PlatformCredentialResponse findById(UUID id);
    
    // get all credentials with optional filtering
    List<PlatformCredentialResponse> findAll(UUID platformId, Boolean isActive);
    
    // delete credential
    void deleteCredential(UUID id);
    
    // * Query Operations
    
    // find credentials by platform ID
    List<PlatformCredentialResponse> findByPlatformId(UUID platformId);
    
    // find credentials by platform ID and active status
    List<PlatformCredentialResponse> findByPlatformIdAndIsActive(UUID platformId, Boolean isActive);
    
    // find credential by platform and key (for unique credential lookup)
    PlatformCredentialResponse findByPlatformIdAndCredentialKey(UUID platformId, String credentialKey);
    
    // find credentials by platform name
    List<PlatformCredentialResponse> findByPlatformName(String platformName);
    
    // find active credentials by platform name
    List<PlatformCredentialResponse> findByPlatformNameAndIsActive(String platformName, Boolean isActive);
    
    // * Secure Credential Operations
    
    // decrypt on demand so operators can retrieve secrets while keeping an audit trail
    String getDecryptedCredentialValue(UUID credentialId);
    
    // verify encryption metadata is intact before expensive background work
    boolean validateCredentialEncryption(UUID credentialId);
    
    // upgrade stored ciphertext to the latest algorithm/key material
    PlatformCredentialResponse reEncryptCredential(UUID credentialId);
    
    // surface credentials whose metadata indicates they require rotation
    List<PlatformCredentialResponse> findCredentialsNeedingReEncryption();
}
