package dev.fincke.hopper.marketplace.client.model;

import java.util.List;
import java.util.Objects;

// * Result DTO
// Represents normalized order responses flowing back from marketplace connectors.
public record OrderResult(
    String orderId,
    String externalId,
    OrderStatus status,
    List<MarketplaceError> errors
)
{
    // * Canonical Constructor
    // Defensive copy so upstream logic cannot mutate stored errors.
    public OrderResult
    {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(errors, "errors");
        errors = List.copyOf(errors);
    }

    // Indicates whether downstream returned business or platform validation issues.
    public boolean hasErrors()
    {
        return !errors().isEmpty();
    }

    // Convenience builder for synthetic failures (e.g., connector timeouts).
    public static OrderResult failed(String orderId, String externalId, MarketplaceError error)
    {
        return new OrderResult(orderId, externalId, OrderStatus.FAILED, List.of(error));
    }
}
