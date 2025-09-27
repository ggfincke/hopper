package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.order.order.dto.OrderCreateRequest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

// Builder for OrderCreateRequest to keep unit tests concise
// Creates order creation DTOs with realistic defaults for e-commerce workflow testing
public final class OrderCreateRequestBuilder
{
    // * Default Test Values
    
    // Random platform ID for marketplace integration testing
    private UUID platformId = UUID.randomUUID();
    // Standard external order ID format for uniqueness constraint testing
    private String externalOrderId = "ORD-1001";
    // Pending status (initial order state in business workflow)
    private String status = "pending";
    // Realistic order total with proper decimal precision
    private BigDecimal totalAmount = new BigDecimal("125.50");
    // Fixed test date for consistent temporal testing
    private Timestamp orderDate = Timestamp.from(Instant.parse("2024-01-01T00:00:00Z"));
    // Buyer ID (null by default, set when testing buyer relationships)
    private UUID buyerId;

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private OrderCreateRequestBuilder()
    {
    }

    // Factory method to start builder chain for order creation requests
    public static OrderCreateRequestBuilder orderCreateRequest()
    {
        return new OrderCreateRequestBuilder();
    }

    // * Builder Methods
    
    // Override platform ID (for testing specific marketplace integrations)
    public OrderCreateRequestBuilder withPlatformId(UUID platformId)
    {
        this.platformId = platformId;
        return this;
    }

    // Override external order ID (for testing uniqueness constraints and duplicates)
    public OrderCreateRequestBuilder withExternalOrderId(String externalOrderId)
    {
        this.externalOrderId = externalOrderId;
        return this;
    }

    // Override status (for testing order state validation and workflows)
    public OrderCreateRequestBuilder withStatus(String status)
    {
        this.status = status;
        return this;
    }

    // Override total amount (for testing payment calculations and validation)
    public OrderCreateRequestBuilder withTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
        return this;
    }

    // Override order date (for testing temporal business logic and reporting)
    public OrderCreateRequestBuilder withOrderDate(Timestamp orderDate)
    {
        this.orderDate = orderDate;
        return this;
    }

    // Set buyer ID (for testing buyer-order relationships and validation)
    public OrderCreateRequestBuilder withBuyerId(UUID buyerId)
    {
        this.buyerId = buyerId;
        return this;
    }

    // * DTO Construction
    
    // Builds OrderCreateRequest DTO for order management API testing
    public OrderCreateRequest build()
    {
        // Create DTO with configured values (validates required fields and relationships)
        return new OrderCreateRequest(platformId, externalOrderId, status, totalAmount, orderDate, buyerId);
    }
}
