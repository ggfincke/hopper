package dev.fincke.hopper.marketplace.service;

import dev.fincke.hopper.marketplace.client.adapter.StubMarketplaceClient;
import dev.fincke.hopper.marketplace.client.model.ListingResult;
import dev.fincke.hopper.marketplace.client.model.ListingStatus;
import dev.fincke.hopper.marketplace.client.model.OrderResult;
import dev.fincke.hopper.marketplace.client.model.OrderStatus;
import dev.fincke.hopper.marketplace.service.dto.AddressDetails;
import dev.fincke.hopper.marketplace.service.dto.BuyerDetails;
import dev.fincke.hopper.marketplace.service.dto.ListingPublicationRequest;
import dev.fincke.hopper.marketplace.service.dto.OrderSubmissionItem;
import dev.fincke.hopper.marketplace.service.dto.OrderSubmissionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// * Tests
// Verifies lightweight mapping and delegation for the default connector service.
class DefaultMarketplaceConnectorServiceTest
{
    // * Fixtures
    private final StubMarketplaceClient stubClient = new StubMarketplaceClient();
    private final MarketplaceConnectorService service = new DefaultMarketplaceConnectorService(stubClient);

    // Publishing a listing should delegate to the underlying client and allow status polling.
    @Test
    void publishListingDelegatesToClient()
    {
        ListingPublicationRequest request = new ListingPublicationRequest(
            "ebay",
            "seller-123",
            "SKU-123",
            "Demo Listing",
            "A sample listing",
            new BigDecimal("15.25"),
            "usd",
            5,
            List.of("https://example.com/image.png")
        );

        ListingResult created = service.publishListing(request);
        assertThat(created.status()).isEqualTo(ListingStatus.PENDING);

        ListingResult polled = service.getListing(created.listingId());
        assertThat(polled.status()).isEqualTo(ListingStatus.ACTIVE);
    }

    // Order submissions should respect idempotency through the connector client.
    @Test
    void submitOrderDelegatesToClient()
    {
        OrderSubmissionItem item = new OrderSubmissionItem("SKU-123", 1, new BigDecimal("15.25"), "USD");
        BuyerDetails buyer = new BuyerDetails(
            "Jane Doe",
            new AddressDetails("123 Main", "Metropolis", "NY", "12345", "US")
        );
        OrderSubmissionRequest request = new OrderSubmissionRequest(
            "amazon",
            "seller-999",
            "listing-abc",
            null,
            buyer,
            List.of(item),
            "order-123"
        );

        OrderResult initial = service.submitOrder(request);
        OrderResult replay = service.submitOrder(request);

        assertThat(initial.orderId()).isEqualTo(replay.orderId());
        assertThat(replay.status()).isEqualTo(OrderStatus.CONFIRMED);
    }
}
