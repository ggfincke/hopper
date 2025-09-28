package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.AddressInfo;

// * DTO
// Serializes buyer addresses for the connector order payloads.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddressPayload(
    String line1,
    String city,
    String region,
    String postal,
    String country
)
{
    // Converts normalized address info into the connector representation.
    public static AddressPayload from(AddressInfo address)
    {
        return new AddressPayload(
            address.line1(),
            address.city(),
            address.region(),
            address.postal(),
            address.country()
        );
    }
}
