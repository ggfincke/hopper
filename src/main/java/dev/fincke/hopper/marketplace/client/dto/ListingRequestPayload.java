package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.ListingCommand;

import java.util.List;

// * DTO
// Outgoing payload for POST /v1/listings on the Go connector.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ListingRequestPayload(
    String platform,
    String sellerAccountId,
    String sku,
    String title,
    String description,
    PricePayload price,
    int quantity,
    List<MediaPayload> media
)
{
    // Creates a connector payload while dropping empty media arrays for readability.
    public static ListingRequestPayload from(ListingCommand command)
    {
        List<MediaPayload> media = command.media().stream()
            .map(MediaPayload::from)
            .toList();
        return new ListingRequestPayload(
            command.platform(),
            command.sellerAccountId(),
            command.sku(),
            command.title(),
            command.description(),
            PricePayload.from(command.price()),
            command.quantity(),
            media.isEmpty() ? null : media
        );
    }
}
