package dev.fincke.hopper.marketplace.client;

import dev.fincke.hopper.marketplace.client.adapter.StubMarketplaceClient;
import dev.fincke.hopper.marketplace.client.model.AddressInfo;
import dev.fincke.hopper.marketplace.client.model.BuyerInfo;
import dev.fincke.hopper.marketplace.client.model.ListingCommand;
import dev.fincke.hopper.marketplace.client.model.ListingResult;
import dev.fincke.hopper.marketplace.client.model.ListingStatus;
import dev.fincke.hopper.marketplace.client.model.MoneyValue;
import dev.fincke.hopper.marketplace.client.model.OrderCommand;
import dev.fincke.hopper.marketplace.client.model.OrderItemCommand;
import dev.fincke.hopper.marketplace.client.model.OrderResult;
import dev.fincke.hopper.marketplace.client.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// * Tests
// Verifies demo-friendly behaviour of the stub marketplace client.
class StubMarketplaceClientTest
{
    // * Fixtures
    // Fresh stub per test to avoid cross-test state leakage.
    private final StubMarketplaceClient client = new StubMarketplaceClient();

    // * Tests
    // Polling should promote listings from PENDING to ACTIVE on subsequent reads.
    @Test
    void createListingTransitionsToActiveOnSecondFetch()
    {
        ListingCommand command = new ListingCommand(
            "ebay",
            "seller-123",
            "SKU-1",
            "Sample Title",
            "Sample Description",
            new MoneyValue(new BigDecimal("10.00"), "usd"),
            1,
            List.of()
        );

        ListingResult created = client.createListing(command);
        assertThat(created.status()).isEqualTo(ListingStatus.PENDING);

        ListingResult fetched = client.getListing(created.listingId());
        assertThat(fetched.status()).isEqualTo(ListingStatus.ACTIVE);
    }

    // Idempotency keys must map repeated submissions to the original order.
    @Test
    void createOrderEnforcesIdempotency()
    {
        BuyerInfo buyer = new BuyerInfo(
            "Jane Doe",
            new AddressInfo("123 Main", "Metropolis", "NY", "12345", "US")
        );
        OrderItemCommand item = new OrderItemCommand("SKU-1", 1, new MoneyValue(new BigDecimal("25.00"), "USD"));
        OrderCommand command = new OrderCommand(
            "amazon",
            "seller-456",
            "listing-1",
            null,
            buyer,
            List.of(item),
            "idemp-123"
        );

        OrderResult first = client.createOrder(command);
        OrderResult second = client.createOrder(command);

        assertThat(first.orderId()).isEqualTo(second.orderId());
        assertThat(second.status()).isEqualTo(OrderStatus.CONFIRMED);
    }

    // Sentinel idempotency keys should surface retry guidance for rate limits.
    @Test
    void createOrderSimulatesRateLimit()
    {
        BuyerInfo buyer = new BuyerInfo(
            "Jane Doe",
            new AddressInfo("123 Main", "Metropolis", "NY", "12345", "US")
        );
        OrderItemCommand item = new OrderItemCommand("SKU-1", 1, new MoneyValue(new BigDecimal("25.00"), "USD"));
        OrderCommand command = new OrderCommand(
            "amazon",
            "seller-456",
            "listing-1",
            null,
            buyer,
            List.of(item),
            "SIM-RATE-001"
        );

        OrderResult result = client.createOrder(command);

        assertThat(result.status()).isEqualTo(OrderStatus.FAILED);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).code()).isEqualTo("RATE_LIMIT");
    }
}
