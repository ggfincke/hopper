package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.catalog.listing.dto.ListingCreateRequest;
import dev.fincke.hopper.catalog.listing.dto.ListingResponse;
import dev.fincke.hopper.catalog.listing.dto.ListingUpdateRequest;
import dev.fincke.hopper.catalog.listing.exception.DuplicateListingException;
import dev.fincke.hopper.catalog.listing.exception.InvalidListingStatusException;
import dev.fincke.hopper.catalog.listing.exception.ListingNotFoundException;
import dev.fincke.hopper.catalog.product.Product;
import dev.fincke.hopper.catalog.product.ProductRepository;
import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for listing business operations.
 * 
 * Handles all listing-related business logic including validation, 
 * status management, and data transformation between entities and DTOs.
 */
@Service
@Transactional(readOnly = true) // default to read-only transactions
public class ListingServiceImpl implements ListingService 
{
    // * Dependencies
    
    // repository for listing data access
    private final ListingRepository listingRepository;
    
    // repository for product data access (to validate product references)
    private final ProductRepository productRepository;
    
    // repository for platform data access (to validate platform references)
    private final PlatformRepository platformRepository;
    
    // * Constructor
    
    public ListingServiceImpl(ListingRepository listingRepository, 
                             ProductRepository productRepository,
                             PlatformRepository platformRepository) 
    {
        this.listingRepository = listingRepository;
        this.productRepository = productRepository;
        this.platformRepository = platformRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional // write operation, requires full transaction
    public ListingResponse createListing(ListingCreateRequest request) 
    {
        // validate product exists
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new IllegalArgumentException("Product with ID " + request.productId() + " not found"));
        
        // validate platform exists
        Platform platform = platformRepository.findById(request.platformId())
            .orElseThrow(() -> new IllegalArgumentException("Platform with ID " + request.platformId() + " not found"));
        
        // validate external listing ID uniqueness for this platform
        if (existsByPlatformAndExternalListingId(platform, request.externalListingId())) 
        {
            throw new DuplicateListingException(request.platformId(), request.externalListingId());
        }
        
        // validate status is not empty after trimming
        if (request.status().trim().isEmpty()) 
        {
            throw new InvalidListingStatusException("Status cannot be empty");
        }
        
        // create and populate entity
        Listing listing = new Listing(
            product,
            platform,
            request.externalListingId(),
            request.status(),
            request.price(),
            request.quantityListed()
        );
        
        // save and return response
        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.from(savedListing);
    }
    
    @Override
    @Transactional
    public ListingResponse updateListing(UUID id, ListingUpdateRequest request) 
    {
        // find existing listing
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        
        // validate that at least one field is provided for update
        if (!request.hasUpdates()) 
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // update external listing ID if provided and different
        if (request.externalListingId() != null && !request.externalListingId().equals(listing.getExternalListingId())) 
        {
            if (existsByPlatformAndExternalListingId(listing.getPlatform(), request.externalListingId())) 
            {
                throw new DuplicateListingException(listing.getPlatform().getId(), request.externalListingId());
            }
            listing.setExternalListingId(request.externalListingId());
        }
        
        // update status if provided
        if (request.status() != null) 
        {
            if (request.status().trim().isEmpty()) 
            {
                throw new InvalidListingStatusException("Status cannot be empty");
            }
            listing.setStatus(request.status());
        }
        
        // update price if provided
        if (request.price() != null) 
        {
            listing.setPrice(request.price()); // BigDecimal scaling handled in entity setter
        }
        
        // update quantity if provided
        if (request.quantityListed() != null) 
        {
            listing.setQuantityListed(request.quantityListed()); // non-negative validation handled in entity setter
        }
        
        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.from(savedListing);
    }
    
    @Override
    public ListingResponse findById(UUID id) 
    {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        return ListingResponse.from(listing);
    }
    
    @Override
    public List<ListingResponse> findAll() 
    {
        return listingRepository.findAll().stream()
            .map(ListingResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteListing(UUID id) 
    {
        if (!listingRepository.existsById(id)) 
        {
            throw new ListingNotFoundException(id);
        }
        
        // TODO: check for dependencies (active orders) before deletion
        listingRepository.deleteById(id);
    }
    
    // * Status Management Operations
    
    @Override
    @Transactional
    public ListingResponse updateStatus(UUID id, String status) 
    {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        
        // validate status is not empty after trimming
        if (status == null || status.trim().isEmpty()) 
        {
            throw new InvalidListingStatusException("Status cannot be empty");
        }
        
        listing.setStatus(status.trim());
        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.from(savedListing);
    }
    
    @Override
    @Transactional
    public ListingResponse activateListing(UUID id) 
    {
        return updateStatus(id, "active");
    }
    
    @Override
    @Transactional
    public ListingResponse deactivateListing(UUID id) 
    {
        return updateStatus(id, "inactive");
    }
    
    // * Query Operations
    
    @Override
    public List<ListingResponse> findByProductId(UUID productId) 
    {
        return listingRepository.findByProductId(productId).stream()
            .map(ListingResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ListingResponse> findByPlatformId(UUID platformId) 
    {
        return listingRepository.findByPlatformId(platformId).stream()
            .map(ListingResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ListingResponse> findByStatus(String status) 
    {
        return listingRepository.findByStatus(status.trim()).stream()
            .map(ListingResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public ListingResponse findByPlatformAndExternalListingId(UUID platformId, String externalListingId) 
    {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform with ID " + platformId + " not found"));
        
        Listing listing = listingRepository.findByPlatformAndExternalListingId(platform, externalListingId.trim())
            .orElseThrow(() -> new ListingNotFoundException(platformId, externalListingId));
        return ListingResponse.from(listing);
    }
    
    // * Price and Quantity Operations
    
    @Override
    @Transactional
    public ListingResponse updatePrice(UUID id, BigDecimal price) 
    {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        
        listing.setPrice(price); // BigDecimal scaling and validation handled in entity setter
        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.from(savedListing);
    }
    
    @Override
    @Transactional
    public ListingResponse updateQuantity(UUID id, int quantityListed) 
    {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        
        listing.setQuantityListed(quantityListed); // non-negative validation handled in entity setter
        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.from(savedListing);
    }
    
    // * Private Helper Methods
    
    // checks if a listing exists by platform and external listing ID (private helper for internal validation)
    private boolean existsByPlatformAndExternalListingId(Platform platform, String externalListingId) 
    {
        return listingRepository.findByPlatformAndExternalListingId(platform, externalListingId.trim()).isPresent();
    }
}
