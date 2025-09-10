package dev.fincke.hopper.platform.platform;

import dev.fincke.hopper.platform.platform.dto.PlatformCreateRequest;
import dev.fincke.hopper.platform.platform.dto.PlatformResponse;
import dev.fincke.hopper.platform.platform.dto.PlatformUpdateRequest;

import java.util.List;
import java.util.UUID;

// Service interface for platform business operations (separates business rules from data access)
public interface PlatformService 
{
    // * Core CRUD Operations
    
    // create new platform
    PlatformResponse createPlatform(PlatformCreateRequest request);
    
    // update existing platform
    PlatformResponse updatePlatform(UUID id, PlatformUpdateRequest request);
    
    // find platform by ID
    PlatformResponse findById(UUID id);
    
    // get all platforms
    List<PlatformResponse> findAll();
    
    // delete platform
    void deletePlatform(UUID id);
    
    // * Query Operations
    
    // find platforms by type
    List<PlatformResponse> findByPlatformType(String platformType);
    
    // find platform by name
    PlatformResponse findByName(String name);
}