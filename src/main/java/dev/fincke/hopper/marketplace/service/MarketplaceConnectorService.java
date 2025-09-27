package dev.fincke.hopper.marketplace.service;

import dev.fincke.hopper.marketplace.client.model.ListingResult;
import dev.fincke.hopper.marketplace.client.model.OrderResult;
import dev.fincke.hopper.marketplace.service.dto.ListingPublicationRequest;
import dev.fincke.hopper.marketplace.service.dto.OrderSubmissionRequest;

// * Interface
// Entry point for orchestrating listing and order interactions with marketplace connectors.
public interface MarketplaceConnectorService
{
    // * Listings
    // Publish a listing using the normalized publication request data.
    ListingResult publishListing(ListingPublicationRequest request);

    // Retrieve the latest status for a marketplace listing by ID.
    ListingResult getListing(String listingId);

    // * Orders
    // Submit an order to the connector, enforcing idempotency via the request payload.
    OrderResult submitOrder(OrderSubmissionRequest request);

    // Retrieve downstream order status for polling or reconciliation flows.
    OrderResult getOrder(String orderId);
}
