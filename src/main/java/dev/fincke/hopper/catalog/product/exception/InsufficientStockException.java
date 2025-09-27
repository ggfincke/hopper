package dev.fincke.hopper.catalog.product.exception;

import java.util.UUID;

// Thrown when a stock update would drop inventory below zero or exceed available reserves.
// Safeguards against overselling by surfacing the shortage to callers.
public class InsufficientStockException extends RuntimeException 
{
    // * Attributes
    
    // Product ID that lacks enough stock for the requested operation
    private final UUID productId;
    
    // Current available stock level when the exception was raised
    private final int availableStock;
    
    // Requested stock amount that could not be fulfilled
    private final int requestedStock;
    
    // * Constructors
    
    public InsufficientStockException(UUID productId, int availableStock, int requestedStock) 
    {
        super(String.format("Insufficient stock for product %s. Available: %d, Requested: %d", 
              productId, availableStock, requestedStock));
        this.productId = productId;
        this.availableStock = availableStock;
        this.requestedStock = requestedStock;
    }
    
    public InsufficientStockException(UUID productId, int availableStock, int requestedStock, String message) 
    {
        super(message);
        this.productId = productId;
        this.availableStock = availableStock;
        this.requestedStock = requestedStock;
    }
    
    public InsufficientStockException(UUID productId, int availableStock, int requestedStock, 
                                    String message, Throwable cause) 
    {
        super(message, cause);
        this.productId = productId;
        this.availableStock = availableStock;
        this.requestedStock = requestedStock;
    }
    
    // * Getters
    
    // Product ID tied to the stock shortage
    public UUID getProductId() 
    {
        return productId;
    }
    
    // Snapshot of the stock level available when the exception occurred
    public int getAvailableStock() 
    {
        return availableStock;
    }
    
    // Amount of stock the caller attempted to reserve or remove
    public int getRequestedStock() 
    {
        return requestedStock;
    }
    
}
