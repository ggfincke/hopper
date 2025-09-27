package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.MarketplaceError;

import java.time.Duration;

// * DTO
// Connector error envelope mirrored on the Java side.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorPayload(String code, String message, String details, Long retryAfterSeconds)
{
    // Converts raw payload values into the normalized MarketplaceError record.
    public MarketplaceError toMarketplaceError()
    {
        Duration retryAfter = retryAfterSeconds == null ? null : Duration.ofSeconds(retryAfterSeconds);
        return new MarketplaceError(code, message, details, retryAfter);
    }
}
