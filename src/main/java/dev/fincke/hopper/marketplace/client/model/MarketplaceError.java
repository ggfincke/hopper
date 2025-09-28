package dev.fincke.hopper.marketplace.client.model;

import java.time.Duration;
import java.util.Objects;

// * Error DTO
// Captures normalized connector failures and retry hints for upstream orchestration.
public record MarketplaceError(String code, String message, String details, Duration retryAfter)
{
    // * Canonical Constructor
    // Keeps defensive null checks so Java callers never see partially populated errors.
    public MarketplaceError
    {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(message, "message");
    }

    // Retry decision helper used by orchestration logic when mapping to resilience policies.
    public boolean isRetryable()
    {
        return "RATE_LIMIT".equals(code) || "RETRYABLE_UPSTREAM".equals(code);
    }
}
