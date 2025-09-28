package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.MoneyValue;

// * DTO
// Serialized representation of price structures sent to the Go connector.
// @JsonInclude trims null entries for optional components.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PricePayload(String amount, String currency)
{
    // Builds the payload from the normalized domain money value.
    public static PricePayload from(MoneyValue value)
    {
        return new PricePayload(value.amount().toPlainString(), value.currency());
    }
}
