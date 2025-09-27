package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.ListingResult;
import dev.fincke.hopper.marketplace.client.model.ListingStatus;
import dev.fincke.hopper.marketplace.client.model.MarketplaceError;

import java.util.List;

// * DTO
// Response structure returned by the Go connector for listing operations.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ListingResponsePayload(
    String listingId,
    String externalId,
    String status,
    List<ErrorPayload> errors
)
{
    // Translates connector payload into internal ListingResult with normalized enums.
    public ListingResult toListingResult()
    {
        List<MarketplaceError> convertedErrors = errors == null ? List.of() : errors.stream()
            .map(ErrorPayload::toMarketplaceError)
            .toList();
        ListingStatus listingStatus = status == null ? ListingStatus.FAILED : ListingStatus.valueOf(status.toUpperCase());
        return new ListingResult(listingId, externalId, listingStatus, convertedErrors);
    }
}
