package dev.fincke.hopper.marketplace.client.model;

import java.util.List;
import java.util.Objects;

// * Result DTO
// Surfaces downstream listing responses back to the domain layer.
public record ListingResult(
    String listingId,
    String externalId,
    ListingStatus status,
    List<MarketplaceError> errors
)
{
    // * Canonical Constructor
    // Copies defensive collections so callers cannot mutate shared state.
    public ListingResult
    {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(errors, "errors");
        errors = List.copyOf(errors);
    }

    // Indicates whether downstream returned validation or platform failures.
    public boolean hasErrors()
    {
        return !errors().isEmpty();
    }

    // Factory used for synthetic failures (e.g., connector transport issues).
    public static ListingResult failed(String listingId, String externalId, MarketplaceError error)
    {
        return new ListingResult(listingId, externalId, ListingStatus.FAILED, List.of(error));
    }
}
