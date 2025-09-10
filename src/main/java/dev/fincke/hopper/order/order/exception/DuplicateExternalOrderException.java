package dev.fincke.hopper.order.order.exception;

import java.util.UUID;

// Domain exception for duplicate external order ID violations (business rule enforcement)
public class DuplicateExternalOrderException extends RuntimeException
{
    
    // Context information for the duplicate violation
    private final UUID platformId;
    private final String externalOrderId;
    private final UUID existingOrderId;
    
    // Constructor for duplicate external order ID error
    public DuplicateExternalOrderException(UUID platformId, String externalOrderId, UUID existingOrderId)
    {
        super(String.format(
            "External order ID '%s' already exists for platform %s (existing order: %s)",
            externalOrderId, platformId, existingOrderId
        ));
        this.platformId = platformId;
        this.externalOrderId = externalOrderId;
        this.existingOrderId = existingOrderId;
    }
    
    // Constructor when existing order ID is not available
    public DuplicateExternalOrderException(UUID platformId, String externalOrderId)
    {
        super(String.format(
            "External order ID '%s' already exists for platform %s",
            externalOrderId, platformId
        ));
        this.platformId = platformId;
        this.externalOrderId = externalOrderId;
        this.existingOrderId = null;
    }
    
    // Platform where the duplicate occurred
    public UUID getPlatformId()
    {
        return platformId;
    }
    
    // External order ID that was duplicated
    public String getExternalOrderId()
    {
        return externalOrderId;
    }
    
    // Existing order ID (null if not available)
    public UUID getExistingOrderId()
    {
        return existingOrderId;
    }
}