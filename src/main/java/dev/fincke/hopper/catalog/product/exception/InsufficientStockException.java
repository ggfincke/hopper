package dev.fincke.hopper.catalog.product.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to reduce product stock below zero
 * or reserve more stock than is available.
 * 
 * Prevents negative inventory which would indicate overselling.
 */
public class InsufficientStockException extends RuntimeException 
{
    // * Attributes
    
    // ID of the product with insufficient stock
    private final UUID productId;
    
    // current available stock
    private final int availableStock;
    
    // requested stock amount
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
    
    // returns the ID of the product with insufficient stock
    public UUID getProductId() 
    {
        return productId;
    }
    
    // returns the current available stock amount
    public int getAvailableStock() 
    {
        return availableStock;
    }
    
    // returns the requested stock amount that couldn't be fulfilled
    public int getRequestedStock() 
    {
        return requestedStock;
    }
    
}