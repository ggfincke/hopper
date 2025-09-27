package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.ListingMedia;

// * DTO
// Wraps listing media entries for connector payloads.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MediaPayload(String url)
{
    // Converts domain media value object to connector representation.
    public static MediaPayload from(ListingMedia media)
    {
        return new MediaPayload(media.url());
    }
}
