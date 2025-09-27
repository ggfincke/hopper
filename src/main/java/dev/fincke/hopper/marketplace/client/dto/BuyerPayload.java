package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.BuyerInfo;

// * DTO
// Buyer wrapper included in order submissions to downstream marketplaces.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuyerPayload(String name, AddressPayload address)
{
    // Converts BuyerInfo into connector payload including nested address serialization.
    public static BuyerPayload from(BuyerInfo buyer)
    {
        return new BuyerPayload(buyer.name(), AddressPayload.from(buyer.address()));
    }
}
