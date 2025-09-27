package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.catalog.product.dto.ProductUpdateRequest;

import java.math.BigDecimal;

// Builder for ProductUpdateRequest with optional fields for tests
// Creates product update DTOs with optional fields for partial update testing
public final class ProductUpdateRequestBuilder
{
    // * Default Test Values (all optional for partial updates)
    
    // SKU (null by default, set only when testing SKU changes)
    private String sku;
    // Name (null by default, set only when testing name updates)
    private String name;
    // Description (null by default, set only when testing content updates)
    private String description;
    // Price (null by default, set only when testing price adjustments)
    private BigDecimal price;
    // Quantity (null by default, set only when testing inventory updates)
    private Integer quantity;

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private ProductUpdateRequestBuilder()
    {
    }

    // Factory method to start builder chain for product update requests
    public static ProductUpdateRequestBuilder productUpdateRequest()
    {
        return new ProductUpdateRequestBuilder();
    }

    // * Builder Methods
    
    // Set SKU (for testing SKU changes and uniqueness constraints)
    public ProductUpdateRequestBuilder withSku(String sku)
    {
        this.sku = sku;
        return this;
    }

    // Set name (for testing display updates and search indexing)
    public ProductUpdateRequestBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    // Set description (for testing content updates and validation)
    public ProductUpdateRequestBuilder withDescription(String description)
    {
        this.description = description;
        return this;
    }

    // Set price (for testing price adjustments and calculation impacts)
    public ProductUpdateRequestBuilder withPrice(BigDecimal price)
    {
        this.price = price;
        return this;
    }

    // Set quantity (for testing inventory adjustments and stock management)
    public ProductUpdateRequestBuilder withQuantity(Integer quantity)
    {
        this.quantity = quantity;
        return this;
    }

    // * DTO Construction
    
    // Builds ProductUpdateRequest DTO for partial update testing
    public ProductUpdateRequest build()
    {
        // Create DTO with only specified fields (supports partial updates)
        return new ProductUpdateRequest(sku, name, description, price, quantity);
    }
}
