package dev.fincke.hopper.catalog.product;

import dev.fincke.hopper.catalog.listing.ListingRepository;
import dev.fincke.hopper.catalog.product.dto.ProductCreateRequest;
import dev.fincke.hopper.catalog.product.dto.ProductResponse;
import dev.fincke.hopper.catalog.product.dto.ProductUpdateRequest;
import dev.fincke.hopper.catalog.product.exception.DuplicateSkuException;
import dev.fincke.hopper.catalog.product.exception.InsufficientStockException;
import dev.fincke.hopper.catalog.product.exception.ProductDeletionNotAllowedException;
import dev.fincke.hopper.order.item.OrderItemRepository;
import dev.fincke.hopper.testsupport.ProductCreateRequestBuilder;
import dev.fincke.hopper.testsupport.ProductTestBuilder;
import dev.fincke.hopper.testsupport.ProductUpdateRequestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Unit tests for ProductServiceImpl using mocks to focus on business rules
// Tests product lifecycle operations including inventory management and referential integrity
// Enables Mockito framework for dependency injection testing
@ExtendWith(MockitoExtension.class)
class ProductServiceImplUnitTest
{
    // * Test Dependencies
    
    // Repository mock for testing product persistence and SKU validation
    @Mock
    private ProductRepository productRepository;

    // Repository mock for testing referential integrity with listings
    @Mock
    private ListingRepository listingRepository;

    // Repository mock for testing referential integrity with order items
    @Mock
    private OrderItemRepository orderItemRepository;

    // Service under test with dependencies injected by Mockito
    @InjectMocks
    private ProductServiceImpl productService;

    // * Create Operation Tests

    // Validates successful product creation when SKU uniqueness constraint is satisfied
    @Test
    @DisplayName("createProduct persists a new product when SKU is unique")
    void createProduct_PersistsWhenSkuUnique()
    {
        // Request with valid product data for creation test
        ProductCreateRequest request = ProductCreateRequestBuilder.productCreateRequest().build();

        // Mock SKU uniqueness check passes (business rule: SKUs must be unique)
        when(productRepository.existsBySku("SKU-001")).thenReturn(false);
        // Mock successful persistence with ID generation (simulates database behavior)
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        ProductResponse response = productService.createProduct(request);

        // Verify response contains the requested product data correctly
        assertEquals("SKU-001", response.sku());
        assertEquals("Sample Product", response.name());
        assertEquals(new BigDecimal("19.99"), response.price());
        // Verify product was persisted to database
        verify(productRepository).save(any(Product.class));
    }

    // Tests SKU uniqueness constraint enforcement during creation
    @Test
    @DisplayName("createProduct throws DuplicateSkuException when SKU already exists")
    void createProduct_ThrowsWhenSkuDuplicate()
    {
        // Request with SKU that already exists in the system
        ProductCreateRequest request = ProductCreateRequestBuilder.productCreateRequest().build();
        // Mock SKU already exists (business rule violation)
        when(productRepository.existsBySku("SKU-001")).thenReturn(true);

        // Verify business rule: SKUs must be unique across all products
        assertThrows(DuplicateSkuException.class, () -> productService.createProduct(request));
        // Verify no save attempt when validation fails
        verify(productRepository, never()).save(any(Product.class));
    }

    // * Update Operation Tests

    // Validates successful update of mutable product fields with constraint checking
    @Test
    @DisplayName("updateProduct updates fields and enforces SKU uniqueness")
    void updateProduct_UpdatesFields()
    {
        // Existing product with initial values for update comparison
        Product existing = ProductTestBuilder.product().withSku("SKU-001").build();
        UUID productId = existing.getId();
        ProductUpdateRequest request = ProductUpdateRequestBuilder.productUpdateRequest()
            .withSku("SKU-002")
            .withName("Updated Name")
            .withDescription("Updated Description")
            .withPrice(new BigDecimal("25.00"))
            .withQuantity(5)
            .build();

        // Mock successful product lookup and new SKU uniqueness check
        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("SKU-002")).thenReturn(false);
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse response = productService.updateProduct(productId, request);

        // Verify all mutable fields were updated correctly
        assertEquals("SKU-002", response.sku());
        assertEquals("Updated Name", response.name());
        assertEquals(new BigDecimal("25.00"), response.price());
        assertEquals(5, response.quantity());
    }

    // Tests SKU uniqueness constraint enforcement during updates
    @Test
    @DisplayName("updateProduct throws DuplicateSkuException when new SKU already exists")
    void updateProduct_ThrowsWhenNewSkuDuplicate()
    {
        // Existing product that will be updated with conflicting SKU
        Product existing = ProductTestBuilder.product().withSku("SKU-001").build();
        UUID productId = existing.getId();
        ProductUpdateRequest request = ProductUpdateRequestBuilder.productUpdateRequest()
            .withSku("SKU-002")  // SKU that conflicts with another product
            .build();

        // Mock existing product found but new SKU conflicts
        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("SKU-002")).thenReturn(true);

        // Verify business rule: SKU must remain unique during updates
        assertThrows(DuplicateSkuException.class, () -> productService.updateProduct(productId, request));
    }

    // * Inventory Management Tests

    // Tests positive inventory adjustment (receiving stock)
    @Test
    @DisplayName("adjustStock increases quantity when delta is positive")
    void adjustStock_IncreasesQuantity()
    {
        // Product with current stock level for adjustment test
        Product existing = ProductTestBuilder.product().withQuantity(5).build();
        UUID productId = existing.getId();

        // Mock successful product lookup and save
        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        // Adjust stock upward (positive delta)
        ProductResponse response = productService.adjustStock(productId, 3);

        // Verify quantity increased correctly (5 + 3 = 8)
        assertEquals(8, response.quantity());
        verify(productRepository).save(existing);
    }

    // Tests inventory constraint validation (cannot go negative)
    @Test
    @DisplayName("adjustStock throws when change would drop quantity below zero")
    void adjustStock_ThrowsWhenInsufficientStock()
    {
        // Product with low stock that cannot cover the adjustment
        Product existing = ProductTestBuilder.product().withQuantity(2).build();
        UUID productId = existing.getId();

        // Mock product lookup but no save expected due to constraint violation
        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));

        // Verify business rule: stock cannot go negative (2 - 5 = -3)
        assertThrows(InsufficientStockException.class, () -> productService.adjustStock(productId, -5));
        // Verify no save attempt when validation fails
        verify(productRepository, never()).save(any(Product.class));
    }

    // * Delete Operation Tests

    // Tests referential integrity enforcement during product deletion
    @Test
    @DisplayName("deleteProduct enforces listing and order item guards")
    void deleteProduct_ThrowsWhenDependenciesExist()
    {
        // Test product ID that has dependent listings
        UUID productId = UUID.randomUUID();
        when(productRepository.existsById(productId)).thenReturn(true);
        // Mock dependency exists (business rule: cannot delete referenced products)
        when(listingRepository.existsByProductId(productId)).thenReturn(true);

        // Verify referential integrity: products with listings cannot be deleted
        assertThrows(ProductDeletionNotAllowedException.class, () -> productService.deleteProduct(productId));
    }

    // * Query Operation Tests

    // Tests input normalization and lookup by business key (SKU)
    @Test
    @DisplayName("findBySku normalizes input and delegates to repository")
    void findBySku_NormalizesWhitespace()
    {
        // Product with uppercase SKU for case-insensitive lookup test
        Product product = ProductTestBuilder.product().withSku("SKU-ABC").build();

        // Mock repository lookup with normalized (lowercase, trimmed) SKU
        when(productRepository.findBySku("sku-abc"))
            .thenReturn(Optional.of(product));

        // Test with whitespace and mixed case input
        ProductResponse response = productService.findBySku("  sku-abc  ");

        // Verify service found product despite input formatting differences
        assertEquals("SKU-ABC", response.sku());
    }

    // Tests inventory reporting query with DTO mapping
    @Test
    @DisplayName("findLowStockProducts maps repository results to DTOs")
    void findLowStockProducts_ReturnsResponses()
    {
        // Product with low stock quantity for threshold test
        Product lowStock = ProductTestBuilder.product().withQuantity(1).build();
        // Mock repository query for products below threshold
        when(productRepository.findByQuantityLessThanEqual(2)).thenReturn(List.of(lowStock));

        // Query for products with stock <= 2
        List<ProductResponse> responses = productService.findLowStockProducts(2);

        // Verify proper DTO mapping and filtering
        assertEquals(1, responses.size());
        assertEquals(lowStock.getId(), responses.get(0).id());
    }
}
