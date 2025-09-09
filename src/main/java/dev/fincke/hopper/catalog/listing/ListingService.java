package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.catalog.listing.dto.ListingCreateRequest;
import dev.fincke.hopper.catalog.listing.dto.ListingResponse;
import dev.fincke.hopper.catalog.listing.dto.ListingUpdateRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for listing business operations.
 * 
 * Handles all listing-related business logic including platform synchronization,
 * status management, and CRUD operations. Separates business rules from data access.
 */
public interface ListingService 
{
    // * Core CRUD Operations
    
    // creates a new listing with business validation
    ListingResponse createListing(ListingCreateRequest request);
    
    // updates an existing listing
    ListingResponse updateListing(UUID id, ListingUpdateRequest request);
    
    // finds listing by ID
    ListingResponse findById(UUID id);
    
    // retrieves all listings
    List<ListingResponse> findAll();
    
    // deletes a listing
    void deleteListing(UUID id);
    
    // * Status Management Operations
    
    // updates listing status with validation
    ListingResponse updateStatus(UUID id, String status);
    
    // activates a listing (sets status to active)
    ListingResponse activateListing(UUID id);
    
    // deactivates a listing (sets status to inactive)
    ListingResponse deactivateListing(UUID id);
    
    // * Query Operations
    
    // finds listings by product ID
    List<ListingResponse> findByProductId(UUID productId);
    
    // finds listings by platform ID
    List<ListingResponse> findByPlatformId(UUID platformId);
    
    // finds listings by status
    List<ListingResponse> findByStatus(String status);
    
    // finds listing by platform and external listing ID
    ListingResponse findByPlatformAndExternalListingId(UUID platformId, String externalListingId);
    
    // * Price and Quantity Operations
    
    // updates listing price
    ListingResponse updatePrice(UUID id, java.math.BigDecimal price);
    
    // updates quantity listed
    ListingResponse updateQuantity(UUID id, int quantityListed);
}
