package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.catalog.listing.dto.ListingCreateRequest;
import dev.fincke.hopper.catalog.listing.dto.ListingResponse;
import dev.fincke.hopper.catalog.listing.dto.ListingUpdateRequest;
import dev.fincke.hopper.catalog.listing.exception.DuplicateListingException;
import dev.fincke.hopper.catalog.listing.exception.InvalidListingStatusException;
import dev.fincke.hopper.catalog.listing.exception.ListingDeletionNotAllowedException;
import dev.fincke.hopper.catalog.listing.exception.ListingNotFoundException;
import dev.fincke.hopper.catalog.product.Product;
import dev.fincke.hopper.catalog.product.ProductRepository;
import dev.fincke.hopper.order.item.OrderItemRepository;
import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import dev.fincke.hopper.testsupport.ListingCreateRequestBuilder;
import dev.fincke.hopper.testsupport.ListingTestBuilder;
import dev.fincke.hopper.testsupport.ListingUpdateRequestBuilder;
import dev.fincke.hopper.testsupport.PlatformTestBuilder;
import dev.fincke.hopper.testsupport.ProductTestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Unit tests for ListingServiceImpl focusing on business logic and validation
// Tests the core listing management operations including creation, updates, and referential integrity
// Enables Mockito framework for dependency injection testing
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ListingServiceImplUnitTest
{
    // * Test Dependencies
    
    // Repository mock for testing listing persistence logic
    @Mock
    private ListingRepository listingRepository;

    // Repository mock for product reference validation
    @Mock
    private ProductRepository productRepository;

    // Repository mock for platform reference validation  
    @Mock
    private PlatformRepository platformRepository;

    // Repository mock for testing referential integrity constraints
    @Mock
    private OrderItemRepository orderItemRepository;

    // Service under test with dependencies injected by Mockito
    @InjectMocks
    private ListingServiceImpl listingService;

    // * Create Operation Tests

    // Validates successful listing creation when all business rules are satisfied
    @Test
    @DisplayName("createListing persists listing when references exist and IDs are unique")
    void createListing_PersistsWhenValid()
    {
        // Test entities with valid references for business rule validation
        Product product = ProductTestBuilder.product().build();
        Platform platform = PlatformTestBuilder.platform().build();
        ListingCreateRequest request = ListingCreateRequestBuilder.listingCreateRequest()
            .withProductId(product.getId())
            .withPlatformId(platform.getId())
            .withExternalListingId("LIST-123")
            .build();

        // Mock successful reference lookups (business rule: references must exist)
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(platformRepository.findById(platform.getId())).thenReturn(Optional.of(platform));
        // Mock no duplicate external ID (business rule: external IDs must be unique per platform)
        when(listingRepository.findByPlatformAndExternalListingId(platform, "LIST-123"))
            .thenReturn(Optional.empty());
        // Mock successful save with ID generation (simulates database persistence)
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> {
            Listing listing = invocation.getArgument(0);
            listing.setId(UUID.randomUUID());
            return listing;
        });

        ListingResponse response = listingService.createListing(request);

        // Verify response contains the requested data correctly mapped
        assertEquals("LIST-123", response.externalListingId());
        assertEquals(platform.getId(), response.platformId());
        assertEquals(product.getId(), response.productId());
        // Verify listing was persisted to database
        verify(listingRepository).save(any(Listing.class));
    }

    // Tests enforcement of unique external ID constraint per platform
    @Test
    @DisplayName("createListing throws when duplicate external ID exists on platform")
    void createListing_ThrowsWhenDuplicate()
    {
        // Setup test data that would create a duplicate external ID
        Product product = ProductTestBuilder.product().build();
        Platform platform = PlatformTestBuilder.platform().build();
        ListingCreateRequest request = ListingCreateRequestBuilder.listingCreateRequest()
            .withProductId(product.getId())
            .withPlatformId(platform.getId())
            .withExternalListingId("LIST-123")
            .build();

        // Mock valid references but existing external ID (business rule violation)
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(platformRepository.findById(platform.getId())).thenReturn(Optional.of(platform));
        // Mock existing listing with same external ID on platform (duplicate constraint)
        when(listingRepository.findByPlatformAndExternalListingId(platform, "LIST-123"))
            .thenReturn(Optional.of(ListingTestBuilder.listing().withPlatform(platform).build()));

        // Verify business rule enforcement: no duplicate external IDs per platform
        assertThrows(DuplicateListingException.class, () -> listingService.createListing(request));
    }

    // Tests validation of required status field for business logic
    @Test
    @DisplayName("createListing throws when status is blank")
    void createListing_ThrowsWhenStatusBlank()
    {
        // Setup test data with invalid blank status
        Product product = ProductTestBuilder.product().build();
        Platform platform = PlatformTestBuilder.platform().build();
        ListingCreateRequest request = ListingCreateRequestBuilder.listingCreateRequest()
            .withProductId(product.getId())
            .withPlatformId(platform.getId())
            .withStatus(" ")  // Blank status violates business rule
            .build();

        // Mock valid references and unique external ID
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(platformRepository.findById(platform.getId())).thenReturn(Optional.of(platform));
        when(listingRepository.findByPlatformAndExternalListingId(platform, request.externalListingId()))
            .thenReturn(Optional.empty());

        // Verify business rule: status cannot be blank or whitespace-only
        assertThrows(InvalidListingStatusException.class, () -> listingService.createListing(request));
    }

    // * Update Operation Tests

    // Validates successful update of mutable listing fields 
    @Test
    @DisplayName("updateListing updates mutable fields and prevents duplicates")
    void updateListing_UpdatesFields()
    {
        // Existing listing with initial values for update comparison
        Listing existing = ListingTestBuilder.listing()
            .withExternalListingId("LIST-123")
            .withStatus("inactive")
            .withPrice(new BigDecimal("19.99"))
            .withQuantityListed(5)
            .build();
        UUID listingId = existing.getId();
        Platform platform = existing.getPlatform();

        // Update request with new values for mutable fields
        ListingUpdateRequest request = ListingUpdateRequestBuilder.listingUpdateRequest()
            .withExternalListingId("LIST-456")
            .withStatus("active")
            .withPrice(new BigDecimal("25.00"))
            .withQuantityListed(10)
            .build();

        // Mock successful listing lookup and no duplicate external ID
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(existing));
        when(listingRepository.findByPlatformAndExternalListingId(platform, "LIST-456"))
            .thenReturn(Optional.empty());
        when(listingRepository.save(existing)).thenReturn(existing);

        ListingResponse response = listingService.updateListing(listingId, request);

        // Verify all mutable fields were updated correctly
        assertEquals("LIST-456", response.externalListingId());
        assertEquals("active", response.status());
        assertEquals(new BigDecimal("25.00"), response.price());
        assertEquals(10, response.quantityListed());
    }

    // Tests update constraint validation for external ID uniqueness
    @Test
    @DisplayName("updateListing throws when new external ID collides")
    void updateListing_ThrowsWhenExternalIdDuplicate()
    {
        // Existing listing that will be updated with conflicting external ID
        Listing existing = ListingTestBuilder.listing().withExternalListingId("LIST-123").build();
        UUID listingId = existing.getId();
        ListingUpdateRequest request = ListingUpdateRequestBuilder.listingUpdateRequest()
            .withExternalListingId("LIST-456")
            .build();

        // Mock existing listing found, but external ID conflicts with another listing
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(existing));
        when(listingRepository.findByPlatformAndExternalListingId(existing.getPlatform(), "LIST-456"))
            .thenReturn(Optional.of(ListingTestBuilder.listing().withPlatform(existing.getPlatform()).build()));

        // Verify business rule: external ID must remain unique during updates
        assertThrows(DuplicateListingException.class, () -> listingService.updateListing(listingId, request));
    }

    // Tests status-only update operation with input normalization
    @Test
    @DisplayName("updateStatus delegates to repository and trims input")
    void updateStatus_UpdatesListing()
    {
        // Existing listing with current status for comparison
        Listing existing = ListingTestBuilder.listing().withStatus("inactive").build();
        UUID listingId = existing.getId();

        // Mock successful listing lookup and save
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(existing));
        when(listingRepository.save(existing)).thenReturn(existing);

        // Test with whitespace input to verify trimming behavior
        ListingResponse response = listingService.updateStatus(listingId, " active ");

        // Verify status was updated and input was trimmed
        assertEquals("active", response.status());
        verify(listingRepository).save(existing);
    }

    // * Delete Operation Tests

    // Tests referential integrity enforcement during deletion
    @Test
    @DisplayName("deleteListing throws when order items reference the listing")
    void deleteListing_ThrowsWhenDependenciesExist()
    {
        // Test listing ID that has dependent order items
        UUID listingId = UUID.randomUUID();
        when(listingRepository.existsById(listingId)).thenReturn(true);
        // Mock dependency exists (business rule: cannot delete referenced listings)
        when(orderItemRepository.existsByListingId(listingId)).thenReturn(true);

        // Verify referential integrity: listings with order items cannot be deleted
        assertThrows(ListingDeletionNotAllowedException.class, () -> listingService.deleteListing(listingId));
    }

    // * Query Operation Tests

    // Tests lookup by business key (platform + external ID) with not found handling
    @Test
    @DisplayName("findByPlatformAndExternalListingId throws when not found")
    void findByPlatformAndExternalListingId_ThrowsWhenMissing()
    {
        // Valid platform for lookup test
        Platform platform = PlatformTestBuilder.platform().build();
        UUID platformId = platform.getId();

        // Mock valid platform but missing listing
        when(platformRepository.findById(platformId)).thenReturn(Optional.of(platform));
        when(listingRepository.findByPlatformAndExternalListingId(platform, "LIST-123"))
            .thenReturn(Optional.empty());

        // Verify proper exception when listing not found by business key
        assertThrows(ListingNotFoundException.class,
            () -> listingService.findByPlatformAndExternalListingId(platformId, "LIST-123"));
    }
}
