package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.catalog.listing.dto.ListingCreateRequest;
import dev.fincke.hopper.catalog.listing.dto.ListingResponse;
import dev.fincke.hopper.catalog.listing.dto.ListingUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for listing management operations.
 * 
 * Provides HTTP endpoints for CRUD operations, status management,
 * and query operations through the service layer.
 */
@RestController
@RequestMapping("/api/listings")
public class ListingController
{
    // * Dependencies
    
    // service for business logic operations
    private final ListingService listingService;

    // * Constructor
    
    public ListingController(ListingService listingService)
    {
        this.listingService = listingService;
    }

    // * Core CRUD Endpoints
    
    @PostMapping
    public ResponseEntity<ListingResponse> createListing(@Valid @RequestBody ListingCreateRequest request)
    {
        ListingResponse response = listingService.createListing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ListingResponse updateListing(@PathVariable UUID id, @Valid @RequestBody ListingUpdateRequest request)
    {
        return listingService.updateListing(id, request);
    }
    
    @GetMapping("/{id}")
    public ListingResponse getById(@PathVariable UUID id)
    {
        return listingService.findById(id);
    }
    
    @GetMapping
    public List<ListingResponse> getAllListings()
    {
        return listingService.findAll();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable UUID id)
    {
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }
    
    // * Status Management Endpoints
    
    @PutMapping("/{id}/status")
    public ListingResponse updateStatus(@PathVariable UUID id, @RequestParam String status)
    {
        return listingService.updateStatus(id, status);
    }
    
    @PostMapping("/{id}/activate")
    public ListingResponse activateListing(@PathVariable UUID id)
    {
        return listingService.activateListing(id);
    }
    
    @PostMapping("/{id}/deactivate") 
    public ListingResponse deactivateListing(@PathVariable UUID id)
    {
        return listingService.deactivateListing(id);
    }
    
    // * Query Endpoints
    
    @GetMapping(params = "productId")
    public List<ListingResponse> getByProductId(@RequestParam UUID productId)
    {
        return listingService.findByProductId(productId);
    }
    
    @GetMapping(params = "platformId")
    public List<ListingResponse> getByPlatformId(@RequestParam UUID platformId)
    {
        return listingService.findByPlatformId(platformId);
    }
    
    @GetMapping(params = "status")
    public List<ListingResponse> getByStatus(@RequestParam String status)
    {
        return listingService.findByStatus(status);
    }
    
    @GetMapping(params = {"platformId", "externalListingId"})
    public ListingResponse getByPlatformAndExternalId(@RequestParam UUID platformId, @RequestParam String externalListingId)
    {
        return listingService.findByPlatformAndExternalListingId(platformId, externalListingId);
    }
    
    // * Price and Quantity Update Endpoints
    
    @PutMapping("/{id}/price")
    public ListingResponse updatePrice(@PathVariable UUID id, @RequestParam BigDecimal price)
    {
        return listingService.updatePrice(id, price);
    }
    
    @PutMapping("/{id}/quantity")
    public ListingResponse updateQuantity(@PathVariable UUID id, @RequestParam int quantityListed)
    {
        return listingService.updateQuantity(id, quantityListed);
    }
}

