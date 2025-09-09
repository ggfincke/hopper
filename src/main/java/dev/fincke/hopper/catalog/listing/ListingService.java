package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.catalog.listing.dto.ListingCreateRequest;
import dev.fincke.hopper.catalog.listing.dto.ListingResponse;
import dev.fincke.hopper.catalog.listing.dto.ListingUpdateRequest;

import java.util.List;
import java.util.UUID;

// Service interface for listing business operations (separates business rules from data access)
public interface ListingService 
{
    // * Core CRUD Operations
    
    // create new listing
    ListingResponse createListing(ListingCreateRequest request);
    
    // update existing listing
    ListingResponse updateListing(UUID id, ListingUpdateRequest request);
    
    // find listing by ID
    ListingResponse findById(UUID id);
    
    // get all listings
    List<ListingResponse> findAll();
    
    // delete listing
    void deleteListing(UUID id);
    
    // * Status Management Operations
    
    // update listing status
    ListingResponse updateStatus(UUID id, String status);
    
    // activate listing
    ListingResponse activateListing(UUID id);
    
    // deactivate listing
    ListingResponse deactivateListing(UUID id);
    
    // * Query Operations
    
    // find listings by product ID
    List<ListingResponse> findByProductId(UUID productId);
    
    // find listings by platform ID
    List<ListingResponse> findByPlatformId(UUID platformId);
    
    // find listings by status
    List<ListingResponse> findByStatus(String status);
    
    // find listing by platform and external listing ID
    ListingResponse findByPlatformAndExternalListingId(UUID platformId, String externalListingId);
    
    // * Price and Quantity Operations
    
    // update listing price
    ListingResponse updatePrice(UUID id, java.math.BigDecimal price);
    
    // update quantity listed
    ListingResponse updateQuantity(UUID id, int quantityListed);
}
