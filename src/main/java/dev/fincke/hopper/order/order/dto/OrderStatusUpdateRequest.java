package dev.fincke.hopper.order.order.dto;

import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;

// Request DTO for updating order status with validation rules
public record OrderStatusUpdateRequest(
    
    // New status value (required)
    @NotBlank(message = "Status is required")
    String status,
    
    // Optional timestamp for status change (defaults to current time)
    Timestamp statusChangeDate,
    
    // Optional reason for status change (for audit trail)
    String reason
    
)
{
    
    // Custom validation for business rules
    public OrderStatusUpdateRequest
    {
        // Trim string fields
        if (status != null)
        {
            status = status.trim();
        }
        
        if (reason != null)
        {
            reason = reason.trim();
        }
        
        // Default to current time if not provided
        if (statusChangeDate == null)
        {
            statusChangeDate = new Timestamp(System.currentTimeMillis());
        }
    }
    
    // Check if reason is provided
    public boolean hasReason()
    {
        return reason != null && !reason.trim().isEmpty();
    }
    
    // Validate status value is meaningful
    public boolean isValid()
    {
        return status != null && !status.trim().isEmpty();
    }
    
    // Common status values (business constants)
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_PAID = "paid";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_SHIPPED = "shipped";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_REFUNDED = "refunded";
    
    // Check if status is a known value
    public boolean isKnownStatus()
    {
        return STATUS_PENDING.equals(status)
            || STATUS_CONFIRMED.equals(status)
            || STATUS_PAID.equals(status)
            || STATUS_PROCESSING.equals(status)
            || STATUS_SHIPPED.equals(status)
            || STATUS_DELIVERED.equals(status)
            || STATUS_CANCELLED.equals(status)
            || STATUS_REFUNDED.equals(status);
    }
}