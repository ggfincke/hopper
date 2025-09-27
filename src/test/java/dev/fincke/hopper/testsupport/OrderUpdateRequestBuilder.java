package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.order.order.dto.OrderUpdateRequest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

// Builder for OrderUpdateRequest allowing tests to specify only the fields under test
// Creates order update DTOs with optional fields for partial update testing
public final class OrderUpdateRequestBuilder
{
    // * Default Test Values (all optional for partial updates)
    
    // External order ID (null by default, set only when testing ID changes)
    private String externalOrderId;
    // Status (null by default, set only when testing status transitions)
    private String status;
    // Total amount (null by default, set only when testing payment updates)
    private BigDecimal totalAmount;
    // Order date (null by default, set only when testing temporal updates)
    private Timestamp orderDate;
    // Buyer ID (null by default, set only when testing buyer reassignment)
    private UUID buyerId;

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private OrderUpdateRequestBuilder()
    {
    }

    // Factory method to start builder chain for order update requests
    public static OrderUpdateRequestBuilder orderUpdateRequest()
    {
        return new OrderUpdateRequestBuilder();
    }

    // * Builder Methods
    
    // Set external order ID (for testing ID changes and uniqueness constraints)
    public OrderUpdateRequestBuilder withExternalOrderId(String externalOrderId)
    {
        this.externalOrderId = externalOrderId;
        return this;
    }

    // Set status (for testing order state transitions and workflow validation)
    public OrderUpdateRequestBuilder withStatus(String status)
    {
        this.status = status;
        return this;
    }

    // Set total amount (for testing payment adjustments and calculation updates)
    public OrderUpdateRequestBuilder withTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
        return this;
    }

    // Set order date (for testing temporal updates and business logic)
    public OrderUpdateRequestBuilder withOrderDate(Timestamp orderDate)
    {
        this.orderDate = orderDate;
        return this;
    }

    // Set buyer ID (for testing buyer reassignment and relationship updates)
    public OrderUpdateRequestBuilder withBuyerId(UUID buyerId)
    {
        this.buyerId = buyerId;
        return this;
    }

    // * Convenience Methods
    
    // Set standard test total amount for payment update testing
    public OrderUpdateRequestBuilder withDefaultTotalAmount()
    {
        this.totalAmount = new BigDecimal("150.00");
        return this;
    }

    // Set standard test date for temporal update testing
    public OrderUpdateRequestBuilder withDefaultOrderDate()
    {
        this.orderDate = Timestamp.from(Instant.parse("2024-01-02T00:00:00Z"));
        return this;
    }

    // * DTO Construction
    
    // Builds OrderUpdateRequest DTO for partial update testing
    public OrderUpdateRequest build()
    {
        // Create DTO with only specified fields (supports partial updates)
        return new OrderUpdateRequest(externalOrderId, status, totalAmount, orderDate, buyerId);
    }
}
