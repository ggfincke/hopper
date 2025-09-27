package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.catalog.product.Product;

import java.math.BigDecimal;
import java.util.UUID;

// Builder for Product entities used in unit tests
// Creates products with realistic defaults for catalog and inventory testing
public final class ProductTestBuilder
{
    // * Default Test Values
    
    // Auto-generated unique ID for each test product
    private UUID id = UUID.randomUUID();
    // Standard SKU format for inventory tracking tests
    private String sku = "SKU-001";
    // Human-readable product name for display testing
    private String name = "Sample Product";
    // Basic description for content validation testing
    private String description = "Sample description";
    // Realistic price with two decimal precision (business requirement)
    private BigDecimal price = new BigDecimal("19.99");
    // Positive stock quantity for inventory management testing
    private int quantity = 10;

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private ProductTestBuilder()
    {
    }

    // Factory method to start builder chain
    public static ProductTestBuilder product()
    {
        return new ProductTestBuilder();
    }

    // * Builder Methods
    
    // Override default ID (useful for relationship and update testing)
    public ProductTestBuilder withId(UUID id)
    {
        this.id = id;
        return this;
    }

    // Override default SKU (for uniqueness constraint and lookup testing)
    public ProductTestBuilder withSku(String sku)
    {
        this.sku = sku;
        return this;
    }

    // Override default name (for search and display functionality testing)
    public ProductTestBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    // Override default description (for content validation testing)
    public ProductTestBuilder withDescription(String description)
    {
        this.description = description;
        return this;
    }

    // Override default price (for pricing validation and calculation testing)
    public ProductTestBuilder withPrice(BigDecimal price)
    {
        this.price = price;
        return this;
    }

    // Override default quantity (for inventory management testing)
    public ProductTestBuilder withQuantity(int quantity)
    {
        this.quantity = quantity;
        return this;
    }

    // * Entity Construction
    
    // Builds Product entity with configured values
    public Product build()
    {
        // Create product using main constructor (validates required fields)
        Product product = new Product(sku, name, price);
        // Set ID manually (simulates database ID assignment)
        product.setId(id);
        // Set optional fields after construction
        product.setDescription(description);
        product.setQuantity(quantity);
        return product;
    }
}
