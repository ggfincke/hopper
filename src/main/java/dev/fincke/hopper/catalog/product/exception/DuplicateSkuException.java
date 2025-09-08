package dev.fincke.hopper.catalog.product.exception;

/**
 * Exception thrown when attempting to create or update a product with a SKU 
 * that already exists in the system.
 * 
 * SKUs must be unique across all products to prevent inventory conflicts.
 */
public class DuplicateSkuException extends RuntimeException 
{
    // * Attributes
    
    // the duplicate SKU that caused the exception
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
    
    // returns the duplicate SKU that caused this exception
    public String getSku() 
    {
        return sku;
    }
    
}