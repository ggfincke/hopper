package dev.fincke.hopper.marketplace.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.fincke.hopper.marketplace.client.model.MarketplaceError;
import dev.fincke.hopper.marketplace.client.model.OrderResult;
import dev.fincke.hopper.marketplace.client.model.OrderStatus;

import java.util.List;

// * DTO
// Response shape returned by the Go connector for order operations.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponsePayload(
    String orderId,
    String externalId,
    String status,
    List<ErrorPayload> errors
)
{
    // Converts connector payload into OrderResult with normalized enum and errors list.
    public OrderResult toOrderResult()
    {
        List<MarketplaceError> convertedErrors = errors == null ? List.of() : errors.stream()
            .map(ErrorPayload::toMarketplaceError)
            .toList();
        OrderStatus orderStatus = status == null ? OrderStatus.FAILED : OrderStatus.valueOf(status.toUpperCase());
        return new OrderResult(orderId, externalId, orderStatus, convertedErrors);
    }
}
