package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.OrderItemCommand;

// * DTO
// Individual order line serialized for connector consumption.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderItemPayload(String sku, int quantity, PricePayload price)
{
    // Converts domain order item commands to connector payload fields.
    public static OrderItemPayload from(OrderItemCommand item)
    {
        return new OrderItemPayload(item.sku(), item.quantity(), PricePayload.from(item.price()));
    }
}
