package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.catalog.listing.dto.ListingCreateRequest;
import dev.fincke.hopper.catalog.listing.dto.ListingResponse;
import dev.fincke.hopper.catalog.listing.dto.ListingUpdateRequest;
import dev.fincke.hopper.catalog.listing.exception.DuplicateListingException;
import dev.fincke.hopper.catalog.listing.exception.InvalidListingStatusException;
import dev.fincke.hopper.catalog.listing.exception.ListingNotFoundException;
import dev.fincke.hopper.catalog.listing.exception.ListingDeletionNotAllowedException;
import dev.fincke.hopper.order.item.OrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

// Service implementation for listing business operations
@Service
@Transactional(readOnly = true) // default to read-only transactions for better performance
@SuppressWarnings("null")
public class ListingServiceImpl implements ListingService 
{
    // * Dependencies
    
    // Spring will inject repository dependencies
    private final ListingRepository listingRepository;
    private final ProductRepository productRepository;
    private final PlatformRepository platformRepository;
    private final OrderItemRepository orderItemRepository;
    
    // * Constructor
    
    public ListingServiceImpl(ListingRepository listingRepository, 
                             ProductRepository productRepository,
                             PlatformRepository platformRepository,
                             OrderItemRepository orderItemRepository) 
    {
        this.listingRepository = listingRepository;
        this.productRepository = productRepository;
        this.platformRepository = platformRepository;
        this.orderItemRepository = orderItemRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional // write operation requires full transaction
    public ListingResponse createListing(ListingCreateRequest request) 
    {
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new IllegalArgumentException("Product with ID " + request.productId() + " not found"));
        
        Platform platform = platformRepository.findById(request.platformId())
            .orElseThrow(() -> new IllegalArgumentException("Platform with ID " + request.platformId() + " not found"));
        
        // check for duplicate external listing ID on platform
        if (existsByPlatformAndExternalListingId(platform, request.externalListingId())) 
        {
            throw new DuplicateListingException(request.platformId(), request.externalListingId());
        }
        
        if (request.status().trim().isEmpty()) 
        {
            throw new InvalidListingStatusException("Status cannot be empty");
        }
        
        Listing listing = new Listing(
            product,
            platform,
            request.externalListingId(),
            request.status(),
            request.price(),
            request.quantityListed()
        );
        
        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.from(savedListing);
    }
    
    @Override
    @Transactional
    public ListingResponse updateListing(UUID id, ListingUpdateRequest request) 
    {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        
        if (!request.hasUpdates()) 
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // update external listing ID if changed
        if (request.externalListingId() != null && !request.externalListingId().equals(listing.getExternalListingId())) 
        {
            if (existsByPlatformAndExternalListingId(listing.getPlatform(), request.externalListingId())) 
            {
                throw new DuplicateListingException(listing.getPlatform().getId(), request.externalListingId());
            }
            listing.setExternalListingId(request.externalListingId());
        }
        
        if (request.status() != null) 
        {
            if (request.status().trim().isEmpty()) 
            {
                throw new InvalidListingStatusException("Status cannot be empty");
            }
            listing.setStatus(request.status());
        }
        
        if (request.price() != null) 
        {
            listing.setPrice(request.price());
        }
        
        if (request.quantityListed() != null) 
        {
            listing.setQuantityListed(request.quantityListed());
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
    public Page<ListingResponse> findAll(Pageable pageable)
    {
        Pageable resolved = pageable == null ? Pageable.unpaged() : pageable;
        return listingRepository.findAll(resolved)
            .map(ListingResponse::from);
    }
    
    @Override
    @Transactional
    public void deleteListing(UUID id) 
    {
        if (!listingRepository.existsById(id)) 
        {
            throw new ListingNotFoundException(id);
        }

        if (orderItemRepository.existsByListingId(id))
        {
            throw new ListingDeletionNotAllowedException(id, "order items reference this listing");
        }

        listingRepository.deleteById(id);
    }
    
    // * Status Management Operations
    
    @Override
    @Transactional
    public ListingResponse updateStatus(UUID id, String status) 
    {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        
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
    public Page<ListingResponse> findByProductId(UUID productId, Pageable pageable)
    {
        Pageable resolved = pageable == null ? Pageable.unpaged() : pageable;
        return listingRepository.findByProductId(productId, resolved)
            .map(ListingResponse::from);
    }
    
    @Override
    public List<ListingResponse> findByPlatformId(UUID platformId) 
    {
        return listingRepository.findByPlatformId(platformId).stream()
            .map(ListingResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public Page<ListingResponse> findByPlatformId(UUID platformId, Pageable pageable)
    {
        Pageable resolved = pageable == null ? Pageable.unpaged() : pageable;
        return listingRepository.findByPlatformId(platformId, resolved)
            .map(ListingResponse::from);
    }
    
    @Override
    public List<ListingResponse> findByStatus(String status) 
    {
        return listingRepository.findByStatus(status.trim()).stream()
            .map(ListingResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public Page<ListingResponse> findByStatus(String status, Pageable pageable) 
    {
        Pageable resolved = pageable == null ? Pageable.unpaged() : pageable;
        return listingRepository.findByStatus(status.trim(), resolved)
            .map(ListingResponse::from);
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
        
        listing.setPrice(price);
        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.from(savedListing);
    }
    
    @Override
    @Transactional
    public ListingResponse updateQuantity(UUID id, int quantityListed) 
    {
        Listing listing = listingRepository.findById(id)
            .orElseThrow(() -> new ListingNotFoundException(id));
        
        listing.setQuantityListed(quantityListed);
        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.from(savedListing);
    }
    
    // * Private Helper Methods
    
    // check if listing exists by platform and external ID
    private boolean existsByPlatformAndExternalListingId(Platform platform, String externalListingId) 
    {
        return listingRepository.findByPlatformAndExternalListingId(platform, externalListingId.trim()).isPresent();
    }
}
