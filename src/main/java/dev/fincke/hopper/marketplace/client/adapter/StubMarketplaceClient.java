package dev.fincke.hopper.marketplace.client.adapter;

import dev.fincke.hopper.marketplace.client.MarketplaceClient;
import dev.fincke.hopper.marketplace.client.model.ListingCommand;
import dev.fincke.hopper.marketplace.client.model.ListingResult;
import dev.fincke.hopper.marketplace.client.model.ListingStatus;
import dev.fincke.hopper.marketplace.client.model.MarketplaceError;
import dev.fincke.hopper.marketplace.client.model.OrderCommand;
import dev.fincke.hopper.marketplace.client.model.OrderResult;
import dev.fincke.hopper.marketplace.client.model.OrderStatus;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// * Stub Implementation
// Provides deterministic behaviour for demos before the Go service is wired in.
public class StubMarketplaceClient implements MarketplaceClient
{
    // * Sentinel Keys (trigger specific error paths during demos)
    private static final String SENTINEL_INVALID = "SIM-INVALID";
    private static final String SENTINEL_RATE_LIMIT = "SIM-RATE";
    private static final String SENTINEL_RETRY = "SIM-RETRY";

    // * In-memory Stores
    // Mimic downstream persistence so idempotency and polling can be exercised locally.
    private final Map<String, StubListing> listings = new ConcurrentHashMap<>();
    private final Map<String, StubOrder> orders = new ConcurrentHashMap<>();
    private final Map<String, String> idempotencyKeys = new ConcurrentHashMap<>();

    @Override
    // Create listing and return a pending status so UI flows can poll to ACTIVE on next read.
    public ListingResult createListing(ListingCommand command)
    {
        Objects.requireNonNull(command, "command");

        Optional<ListingResult> error = simulateListingError(command);
        if (error.isPresent())
        {
            return error.get();
        }

        String listingId = UUID.randomUUID().toString();
        String externalId = "%s-%s".formatted(command.platform(), command.sku());
        StubListing stored = new StubListing(listingId, externalId);
        listings.put(listingId, stored);
        return stored.toResult();
    }

    @Override
    // Single polling hop flips the listing to ACTIVE to simulate connector progression.
    public ListingResult getListing(String listingId)
    {
        Objects.requireNonNull(listingId, "listingId");
        StubListing stored = listings.get(listingId);
        if (stored == null)
        {
            MarketplaceError error = new MarketplaceError(
                "NOT_FOUND",
                "Listing not found",
                null,
                null
            );
            return new ListingResult(listingId, null, ListingStatus.FAILED, List.of(error));
        }
        return stored.advanceStatusAndToResult();
    }

    @Override
    // Generates idempotent order confirmations with optional error simulation.
    public OrderResult createOrder(OrderCommand command)
    {
        Objects.requireNonNull(command, "command");

        String existingOrderId = idempotencyKeys.get(command.idempotencyKey());
        if (existingOrderId != null)
        {
            return orders.get(existingOrderId).toResult();
        }

        Optional<OrderResult> error = simulateOrderError(command);
        if (error.isPresent())
        {
            return error.get();
        }

        String orderId = UUID.randomUUID().toString();
        String externalId = "%s-%s".formatted(command.platform(), UUID.randomUUID());
        StubOrder stored = new StubOrder(orderId, externalId);
        orders.put(orderId, stored);
        idempotencyKeys.put(command.idempotencyKey(), orderId);
        return stored.toResult();
    }

    @Override
    // Returns stored order state or a NOT_FOUND error envelope for demo coverage.
    public OrderResult getOrder(String orderId)
    {
        Objects.requireNonNull(orderId, "orderId");
        StubOrder stored = orders.get(orderId);
        if (stored == null)
        {
            MarketplaceError error = new MarketplaceError(
                "NOT_FOUND",
                "Order not found",
                null,
                null
            );
            return new OrderResult(orderId, null, OrderStatus.FAILED, List.of(error));
        }
        return stored.toResult();
    }

    // * Error Simulation Helpers
    // SKU/idempotency suffixes drive predictable error responses for test scenarios.
    private Optional<ListingResult> simulateListingError(ListingCommand command)
    {
        if (command.sku().contains(SENTINEL_INVALID))
        {
            MarketplaceError error = new MarketplaceError(
                "INVALID_REQUEST",
                "Listing SKU flagged as invalid",
                "sku",
                null
            );
            return Optional.of(ListingResult.failed(null, null, error));
        }
        if (command.sku().contains(SENTINEL_RATE_LIMIT))
        {
            MarketplaceError error = new MarketplaceError(
                "RATE_LIMIT",
                "Rate limited by downstream",
                null,
                Duration.ofSeconds(2)
            );
            return Optional.of(ListingResult.failed(null, null, error));
        }
        return Optional.empty();
    }

    private Optional<OrderResult> simulateOrderError(OrderCommand command)
    {
        if (command.idempotencyKey().contains(SENTINEL_INVALID))
        {
            MarketplaceError error = new MarketplaceError(
                "INVALID_REQUEST",
                "Order payload flagged as invalid",
                "idempotencyKey",
                null
            );
            return Optional.of(OrderResult.failed(null, null, error));
        }
        if (command.idempotencyKey().contains(SENTINEL_RATE_LIMIT))
        {
            MarketplaceError error = new MarketplaceError(
                "RATE_LIMIT",
                "Rate limited by downstream",
                null,
                Duration.ofSeconds(5)
            );
            return Optional.of(OrderResult.failed(null, null, error));
        }
        if (command.idempotencyKey().contains(SENTINEL_RETRY))
        {
            MarketplaceError error = new MarketplaceError(
                "RETRYABLE_UPSTREAM",
                "Transient upstream failure",
                null,
                Duration.ofSeconds(3)
            );
            return Optional.of(OrderResult.failed(null, null, error));
        }
        return Optional.empty();
    }

    // * Internal View Models
    // Minimal state holders to mimic connector persistence behaviour.
    private static final class StubListing
    {
        private final String listingId;
        private final String externalId;
        private ListingStatus status = ListingStatus.PENDING;

        private StubListing(String listingId, String externalId)
        {
            this.listingId = listingId;
            this.externalId = externalId;
        }

        private synchronized ListingResult advanceStatusAndToResult()
        {
            if (status == ListingStatus.PENDING)
            {
                status = ListingStatus.ACTIVE;
            }
            return toResult();
        }

        private ListingResult toResult()
        {
            return new ListingResult(listingId, externalId, status, List.of());
        }
    }

    private static final class StubOrder
    {
        private final String orderId;
        private final String externalId;
        private final List<MarketplaceError> errors = new ArrayList<>();
        private OrderStatus status = OrderStatus.CONFIRMED;

        private StubOrder(String orderId, String externalId)
        {
            this.orderId = orderId;
            this.externalId = externalId;
        }

        private OrderResult toResult()
        {
            return new OrderResult(orderId, externalId, status, List.copyOf(errors));
        }
    }
}
