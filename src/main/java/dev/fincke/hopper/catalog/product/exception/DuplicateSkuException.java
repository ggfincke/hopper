package dev.fincke.hopper.catalog.product.exception;

import dev.fincke.hopper.api.error.ConflictException;

// Raised when a product create/update reuses an existing SKU.
// Enforces SKU uniqueness so downstream systems see one product per identifier.
public class DuplicateSkuException extends ConflictException 
{
    // * Attributes
    
    // Conflicting SKU value supplied by the client
    private final String sku;
    
    // * Constructors
    
    public DuplicateSkuException(String sku) 
    {
        super("Product with SKU '" + sku + "' already exists");
        this.sku = sku;
    }
    
    public DuplicateSkuException(String sku, String message) 
    {
        super(message);
        this.sku = sku;
    }
    
    public DuplicateSkuException(String sku, String message, Throwable cause) 
    {
        super(message, cause);
        this.sku = sku;
    }
    
    // * Getters
    
    // Conflicting SKU that triggered the uniqueness check
    public String getSku() 
    {
        return sku;
    }
    
}
