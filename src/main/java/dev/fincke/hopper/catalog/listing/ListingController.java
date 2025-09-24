package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.catalog.listing.dto.ListingCreateRequest;
import dev.fincke.hopper.catalog.listing.dto.ListingResponse;
import dev.fincke.hopper.catalog.listing.dto.ListingUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

// REST controller handling listing API endpoints
@RestController
@RequestMapping("/api/listings")
public class ListingController
{
    // * Dependencies
    
    // Spring will inject service dependency
    private final ListingService listingService;

    // * Constructor
    
    public ListingController(ListingService listingService)
    {
        this.listingService = listingService;
    }

    // * Core CRUD Endpoints
    
    // POST /api/listings - create new listing
    @PostMapping
    public ResponseEntity<ListingResponse> createListing(@Valid @RequestBody ListingCreateRequest request)
    {
        ListingResponse response = listingService.createListing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // PUT /api/listings/{id} - update existing listing
    @PutMapping("/{id}")
    public ListingResponse updateListing(@PathVariable UUID id, @Valid @RequestBody ListingUpdateRequest request)
    {
        return listingService.updateListing(id, request);
    }
    
    // GET /api/listings/{id} - get listing by ID
    @GetMapping("/{id}")
    public ListingResponse getById(@PathVariable UUID id)
    {
        return listingService.findById(id);
    }
    
    // GET /api/listings - get all listings
    @GetMapping
    public Page<ListingResponse> getAllListings(@PageableDefault(size = 20) Pageable pageable)
    {
        return listingService.findAll(pageable);
    }
    
    // DELETE /api/listings/{id} - delete listing
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable UUID id)
    {
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }
    
    // * Status Management Endpoints
    
    // PUT /api/listings/{id}/status - update listing status
    @PutMapping("/{id}/status")
    public ListingResponse updateStatus(@PathVariable UUID id, @RequestParam String status)
    {
        return listingService.updateStatus(id, status);
    }
    
    // POST /api/listings/{id}/activate - activate listing
    @PostMapping("/{id}/activate")
    public ListingResponse activateListing(@PathVariable UUID id)
    {
        return listingService.activateListing(id);
    }
    
    // POST /api/listings/{id}/deactivate - deactivate listing
    @PostMapping("/{id}/deactivate") 
    public ListingResponse deactivateListing(@PathVariable UUID id)
    {
        return listingService.deactivateListing(id);
    }
    
    // * Query Endpoints
    
    // GET /api/listings?productId={id} - find listings by product
    @GetMapping(params = "productId")
    public Page<ListingResponse> getByProductId(@RequestParam UUID productId,
                                             @PageableDefault(size = 20) Pageable pageable)
    {
        return listingService.findByProductId(productId, pageable);
    }
    
    // GET /api/listings?platformId={id} - find listings by platform
    @GetMapping(params = "platformId")
    public Page<ListingResponse> getByPlatformId(@RequestParam UUID platformId,
                                          @PageableDefault(size = 20) Pageable pageable)
    {
        return listingService.findByPlatformId(platformId, pageable);
    }
    
    // GET /api/listings?status={status} - find listings by status
    @GetMapping(params = "status")
    public Page<ListingResponse> getByStatus(@RequestParam String status,
                                     @PageableDefault(size = 20) Pageable pageable)
    {
        return listingService.findByStatus(status, pageable);
    }
    
    // GET /api/listings?platformId={id}&externalListingId={id} - find by platform and external ID
    @GetMapping(params = {"platformId", "externalListingId"})
    public ListingResponse getByPlatformAndExternalId(@RequestParam UUID platformId, @RequestParam String externalListingId)
    {
        return listingService.findByPlatformAndExternalListingId(platformId, externalListingId);
    }
    
    // * Price and Quantity Update Endpoints
    
    // PUT /api/listings/{id}/price - update listing price
    @PutMapping("/{id}/price")
    public ListingResponse updatePrice(@PathVariable UUID id, @RequestParam BigDecimal price)
    {
        return listingService.updatePrice(id, price);
    }
    
    // PUT /api/listings/{id}/quantity - update listing quantity
    @PutMapping("/{id}/quantity")
    public ListingResponse updateQuantity(@PathVariable UUID id, @RequestParam int quantityListed)
    {
        return listingService.updateQuantity(id, quantityListed);
    }
}

