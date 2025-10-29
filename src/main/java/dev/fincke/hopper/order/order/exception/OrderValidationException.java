package dev.fincke.hopper.order.order.exception;

import dev.fincke.hopper.api.error.BadRequestException;

import java.util.List;
import java.util.UUID;

// Domain exception for order validation failures (business rule violations)
public class OrderValidationException extends BadRequestException
{
    
    // Context information for validation failure
    private final UUID orderId;
    private final List<String> validationErrors;
    private final String field;
    
    // Constructor for single validation error
    public OrderValidationException(String message)
    {
        super(message);
        this.orderId = null;
        this.validationErrors = List.of(message);
        this.field = null;
    }
    
    // Constructor for field-specific validation error
    public OrderValidationException(String field, String message)
    {
        super(String.format("Validation error for field '%s': %s", field, message));
        this.orderId = null;
        this.validationErrors = List.of(message);
        this.field = field;
    }
    
    // Constructor for multiple validation errors
    public OrderValidationException(List<String> validationErrors)
    {
        super("Order validation failed: " + String.join("; ", validationErrors));
        this.orderId = null;
        this.validationErrors = List.copyOf(validationErrors);
        this.field = null;
    }
    
    // Constructor for order-specific validation errors
    public OrderValidationException(UUID orderId, List<String> validationErrors)
    {
        super(String.format("Validation failed for order %s: %s", 
            orderId, String.join("; ", validationErrors)));
        this.orderId = orderId;
        this.validationErrors = List.copyOf(validationErrors);
        this.field = null;
    }
    
    // Order ID (null for general validation)
    public UUID getOrderId()
    {
        return orderId;
    }
    
    // List of validation error messages
    public List<String> getValidationErrors()
    {
        return validationErrors;
    }
    
    // Field name (null if not field-specific)
    public String getField()
    {
        return field;
    }
    
    // Check if there are multiple errors
    public boolean hasMultipleErrors()
    {
        return validationErrors.size() > 1;
    }
}