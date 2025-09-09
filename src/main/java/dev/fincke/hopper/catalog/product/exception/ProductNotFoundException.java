package dev.fincke.hopper.catalog.product.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested product cannot be found in the system.
 * 
 * Used for operations that require an existing product to be present.
 */
public class ProductNotFoundException extends RuntimeException 
{
    // * Attributes
    
    // ID of the product that was not found
    private final UUID productId;
    
    // * Constructors
    
    public ProductNotFoundException(UUID productId) 
    {
        super("Product with ID " + productId + " not found");
        this.productId = productId;
    }
    
    public ProductNotFoundException(String sku) 
    {
        super("Product with SKU '" + sku + "' not found");
        this.productId = null;
    }
    
    public ProductNotFoundException(UUID productId, String message) 
    {
        super(message);
        this.productId = productId;
    }
    
    public ProductNotFoundException(UUID productId, String message, Throwable cause) 
    {
        super(message, cause);
        this.productId = productId;
    }
    
    // * Getters
    
    // returns the ID of the product that was not found (may be null for SKU-based lookups)
    public UUID getProductId() 
    {
        return productId;
    }
    
}