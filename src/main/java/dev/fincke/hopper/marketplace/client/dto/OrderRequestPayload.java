package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.OrderCommand;

import java.util.List;

// * DTO
// Outgoing payload for POST /v1/orders against the Go connector.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderRequestPayload(
    String platform,
    String sellerAccountId,
    String listingId,
    String sku,
    BuyerPayload buyer,
    List<OrderItemPayload> items,
    String idempotencyKey
)
{
    // Converts normalized OrderCommand into the transport shape expected by the connector.
    public static OrderRequestPayload from(OrderCommand command)
    {
        BuyerPayload buyerPayload = command.buyer() == null ? null : BuyerPayload.from(command.buyer());
        List<OrderItemPayload> itemPayloads = command.items().stream()
            .map(OrderItemPayload::from)
            .toList();
        return new OrderRequestPayload(
            command.platform(),
            command.sellerAccountId(),
            command.listingId(),
            command.sku(),
            buyerPayload,
            itemPayloads,
            command.idempotencyKey()
        );
    }
}
