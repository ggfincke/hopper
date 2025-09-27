package dev.fincke.hopper.catalog.product.exception;

import java.util.UUID;

// Raised when a product cannot be removed because dependent records still exist
public class ProductDeletionNotAllowedException extends RuntimeException
{
    // ID of the product that cannot be deleted
    private final UUID productId;

    public ProductDeletionNotAllowedException(UUID productId, String reason)
    {
        super("Cannot delete product " + productId + ": " + reason);
        this.productId = productId;
    }

    public UUID getProductId()
    {
        return productId;
    }
}
