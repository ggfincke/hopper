package dev.fincke.hopper.catalog.product.exception;

import dev.fincke.hopper.api.error.NotFoundException;

import java.util.UUID;

// Signals that a requested product could not be found.
// Allows callers to translate missing resources into appropriate API responses.
public class ProductNotFoundException extends NotFoundException 
{
    // * Attributes
    
    // Product ID that was requested but not found (null when looked up by SKU)
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
    
    // Product ID associated with the missing resource (may be null)
    public UUID getProductId() 
    {
        return productId;
    }
    
}
