package dev.fincke.hopper.marketplace.client.adapter;

import dev.fincke.hopper.marketplace.client.MarketplaceClient;
import dev.fincke.hopper.marketplace.client.dto.ListingRequestPayload;
import dev.fincke.hopper.marketplace.client.dto.ListingResponsePayload;
import dev.fincke.hopper.marketplace.client.dto.OrderRequestPayload;
import dev.fincke.hopper.marketplace.client.dto.OrderResponsePayload;
import dev.fincke.hopper.marketplace.client.model.ListingCommand;
import dev.fincke.hopper.marketplace.client.model.ListingResult;
import dev.fincke.hopper.marketplace.client.model.MarketplaceError;
import dev.fincke.hopper.marketplace.client.model.OrderCommand;
import dev.fincke.hopper.marketplace.client.model.OrderResult;
import dev.fincke.hopper.marketplace.client.model.ListingStatus;
import dev.fincke.hopper.marketplace.client.model.OrderStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

// * Remote Implementation
// Bridges Java orchestration and the Go connector over REST/JSON.
// NOTE: The Go connector currently returns stubbed placeholders for eBay/TCGPlayer until live APIs are integrated.
@SuppressWarnings("null")
public class RemoteGoMarketplaceClient implements MarketplaceClient
{
    private final RestClient restClient;

    // Injected RestClient carries base URL, timeouts, and auth headers from configuration.
    public RemoteGoMarketplaceClient(RestClient restClient)
    {
        this.restClient = restClient;
    }

    @Override
    // POST /v1/listings - forwards normalized listing payloads to the connector.
    public ListingResult createListing(ListingCommand command)
    {
        try
        {
            ListingResponsePayload payload = restClient.post()
                .uri("/v1/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ListingRequestPayload.from(command))
                .retrieve()
                .body(ListingResponsePayload.class);
            return payload == null
                ? ListingResult.failed(null, null, unknownError("Empty response from connector"))
                : payload.toListingResult();
        }
        catch (RestClientException ex)
        {
            return ListingResult.failed(null, null, unknownError(ex.getMessage()));
        }
    }

    @Override
    // GET /v1/listings/{id} - retrieves latest listing status for polling flows.
    public ListingResult getListing(String listingId)
    {
        try
        {
            ListingResponsePayload payload = restClient.get()
                .uri("/v1/listings/{id}", listingId)
                .retrieve()
                .body(ListingResponsePayload.class);
            return payload == null
                ? new ListingResult(listingId, null, ListingStatus.FAILED, List.of(unknownError("Empty response from connector")))
                : payload.toListingResult();
        }
        catch (RestClientException ex)
        {
            return new ListingResult(listingId, null, ListingStatus.FAILED, List.of(unknownError(ex.getMessage())));
        }
    }

    @Override
    // POST /v1/orders - submits order create requests carrying idempotency keys.
    public OrderResult createOrder(OrderCommand command)
    {
        try
        {
            OrderResponsePayload payload = restClient.post()
                .uri("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(OrderRequestPayload.from(command))
                .retrieve()
                .body(OrderResponsePayload.class);
            return payload == null
                ? OrderResult.failed(null, null, unknownError("Empty response from connector"))
                : payload.toOrderResult();
        }
        catch (RestClientException ex)
        {
            return OrderResult.failed(null, null, unknownError(ex.getMessage()))
                ;
        }
    }

    @Override
    // GET /v1/orders/{id} - fetches downstream order status snapshots.
    public OrderResult getOrder(String orderId)
    {
        try
        {
            OrderResponsePayload payload = restClient.get()
                .uri("/v1/orders/{id}", orderId)
                .retrieve()
                .body(OrderResponsePayload.class);
            return payload == null
                ? new OrderResult(orderId, null, OrderStatus.FAILED, List.of(unknownError("Empty response from connector")))
                : payload.toOrderResult();
        }
        catch (RestClientException ex)
        {
            return new OrderResult(orderId, null, OrderStatus.FAILED, List.of(unknownError(ex.getMessage())));
        }
    }

    // Wrap connector transport issues in a normalized UNKNOWN error envelope.
    private static MarketplaceError unknownError(String message)
    {
        return new MarketplaceError("UNKNOWN", message == null ? "Connector call failed" : message, null, null);
    }
}
