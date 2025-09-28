package dev.fincke.hopper.marketplace.client;

import dev.fincke.hopper.marketplace.client.model.ListingCommand;
import dev.fincke.hopper.marketplace.client.model.ListingResult;
import dev.fincke.hopper.marketplace.client.model.OrderCommand;
import dev.fincke.hopper.marketplace.client.model.OrderResult;

// * Interface
// Contract between the Java orchestration layer and marketplace connectors.
public interface MarketplaceClient
{
    // * Listing Operations
    // Create a listing using normalized DTOs so downstream connectors stay decoupled from domain models.
    ListingResult createListing(ListingCommand command);

    // Retrieve current listing status to support polling flows during demos.
    ListingResult getListing(String listingId);

    // * Order Operations
    // Submit an order request while preserving idempotency metadata across services.
    OrderResult createOrder(OrderCommand command);

    // Fetch downstream order status for confirmation flows and error surfacing.
    OrderResult getOrder(String orderId);
}
