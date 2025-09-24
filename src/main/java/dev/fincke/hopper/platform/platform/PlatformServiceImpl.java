package dev.fincke.hopper.platform.platform;

import dev.fincke.hopper.platform.platform.dto.PlatformCreateRequest;
import dev.fincke.hopper.platform.platform.dto.PlatformResponse;
import dev.fincke.hopper.platform.platform.dto.PlatformUpdateRequest;
import dev.fincke.hopper.platform.platform.exception.DuplicatePlatformNameException;
import dev.fincke.hopper.platform.platform.exception.PlatformNotFoundException;
import dev.fincke.hopper.platform.platform.exception.PlatformDeletionNotAllowedException;
import dev.fincke.hopper.catalog.listing.ListingRepository;
import dev.fincke.hopper.platform.credential.PlatformCredentialRepository;
import dev.fincke.hopper.order.order.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service implementation for platform business operations
@Service
@Transactional(readOnly = true) // default to read-only transactions for better performance
public class PlatformServiceImpl implements PlatformService 
{
    // * Dependencies
    
    // Spring will inject repository dependency
    private final PlatformRepository platformRepository;
    private final ListingRepository listingRepository;
    private final PlatformCredentialRepository credentialRepository;
    private final OrderRepository orderRepository;
    
    // * Constructor
    
    public PlatformServiceImpl(PlatformRepository platformRepository,
                                 ListingRepository listingRepository,
                                 PlatformCredentialRepository credentialRepository,
                                 OrderRepository orderRepository) 
    {
        this.platformRepository = platformRepository;
        this.listingRepository = listingRepository;
        this.credentialRepository = credentialRepository;
        this.orderRepository = orderRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional // write operation requires full transaction
    public PlatformResponse createPlatform(PlatformCreateRequest request) 
    {
        // check for duplicate name (business rule: platform names must be unique)
        Platform existingPlatform = platformRepository.findByName(request.name());
        if (existingPlatform != null) 
        {
            throw new DuplicatePlatformNameException(request.name());
        }
        
        Platform platform = new Platform(
            request.name(),
            request.platformType()
        );
        
        Platform savedPlatform = platformRepository.save(platform);
        return PlatformResponse.from(savedPlatform);
    }
    
    @Override
    @Transactional
    public PlatformResponse updatePlatform(UUID id, PlatformUpdateRequest request) 
    {
        Platform platform = platformRepository.findById(id)
            .orElseThrow(() -> new PlatformNotFoundException(id));
        
        if (!request.hasUpdates()) 
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // update name if changed (check for duplicate)
        if (request.name() != null && !request.name().equals(platform.getName())) 
        {
            Platform existingPlatform = platformRepository.findByName(request.name());
            if (existingPlatform != null) 
            {
                throw new DuplicatePlatformNameException(request.name());
            }
            platform.setName(request.name());
        }
        
        if (request.platformType() != null) 
        {
            platform.setPlatformType(request.platformType());
        }
        
        Platform savedPlatform = platformRepository.save(platform);
        return PlatformResponse.from(savedPlatform);
    }
    
    @Override
    public PlatformResponse findById(UUID id) 
    {
        Platform platform = platformRepository.findById(id)
            .orElseThrow(() -> new PlatformNotFoundException(id));
        return PlatformResponse.from(platform);
    }
    
    @Override
    public List<PlatformResponse> findAll() 
    {
        return platformRepository.findAll().stream()
            .map(PlatformResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deletePlatform(UUID id) 
    {
        if (!platformRepository.existsById(id)) 
        {
            throw new PlatformNotFoundException(id);
        }

        if (listingRepository.existsByPlatformId(id))
        {
            throw new PlatformDeletionNotAllowedException(id, "listings still reference this platform");
        }

        if (credentialRepository.existsByPlatformId(id))
        {
            throw new PlatformDeletionNotAllowedException(id, "credentials are still stored for this platform");
        }

        if (orderRepository.existsByPlatformId(id))
        {
            throw new PlatformDeletionNotAllowedException(id, "orders still reference this platform");
        }

        platformRepository.deleteById(id);
    }
    
    // * Query Operations
    
    @Override
    public List<PlatformResponse> findByPlatformType(String platformType) 
    {
        return platformRepository.findByPlatformType(platformType.trim()).stream()
            .map(PlatformResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public PlatformResponse findByName(String name) 
    {
        Platform platform = platformRepository.findByName(name.trim());
        if (platform == null) 
        {
            throw new PlatformNotFoundException(name);
        }
        return PlatformResponse.from(platform);
    }
}