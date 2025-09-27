package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.catalog.product.dto.ProductCreateRequest;

import java.math.BigDecimal;

// Builder for ProductCreateRequest documents in tests
// Creates product creation DTOs with realistic defaults for catalog management testing
public final class ProductCreateRequestBuilder
{
    // * Default Test Values
    
    // Standard SKU format for inventory tracking and uniqueness testing
    private String sku = "SKU-001";
    // Human-readable product name for display and search testing
    private String name = "Sample Product";
    // Basic description for content validation testing
    private String description = "Sample description";
    // Realistic price with proper decimal precision (business requirement)
    private BigDecimal price = new BigDecimal("19.99");
    // Positive initial quantity for inventory management testing
    private int quantity = 10;

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private ProductCreateRequestBuilder()
    {
    }

    // Factory method to start builder chain for product creation requests
    public static ProductCreateRequestBuilder productCreateRequest()
    {
        return new ProductCreateRequestBuilder();
    }

    // * Builder Methods
    
    // Override SKU (for testing uniqueness constraints and format validation)
    public ProductCreateRequestBuilder withSku(String sku)
    {
        this.sku = sku;
        return this;
    }

    // Override name (for testing display functionality and search capabilities)
    public ProductCreateRequestBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    // Override description (for testing content validation and storage)
    public ProductCreateRequestBuilder withDescription(String description)
    {
        this.description = description;
        return this;
    }

    // Override price (for testing pricing validation and decimal precision)
    public ProductCreateRequestBuilder withPrice(BigDecimal price)
    {
        this.price = price;
        return this;
    }

    // Override quantity (for testing inventory initialization and constraints)
    public ProductCreateRequestBuilder withQuantity(int quantity)
    {
        this.quantity = quantity;
        return this;
    }

    // * DTO Construction
    
    // Builds ProductCreateRequest DTO for product catalog API testing
    public ProductCreateRequest build()
    {
        // Create DTO with configured values (validates all required fields)
        return new ProductCreateRequest(sku, name, description, price, quantity);
    }
}
