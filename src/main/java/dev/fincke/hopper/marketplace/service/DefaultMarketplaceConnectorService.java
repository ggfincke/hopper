package dev.fincke.hopper.marketplace.service;

import dev.fincke.hopper.marketplace.client.MarketplaceClient;
import dev.fincke.hopper.marketplace.client.model.AddressInfo;
import dev.fincke.hopper.marketplace.client.model.BuyerInfo;
import dev.fincke.hopper.marketplace.client.model.ListingCommand;
import dev.fincke.hopper.marketplace.client.model.ListingMedia;
import dev.fincke.hopper.marketplace.client.model.ListingResult;
import dev.fincke.hopper.marketplace.client.model.MoneyValue;
import dev.fincke.hopper.marketplace.client.model.OrderCommand;
import dev.fincke.hopper.marketplace.client.model.OrderItemCommand;
import dev.fincke.hopper.marketplace.client.model.OrderResult;
import dev.fincke.hopper.marketplace.service.dto.AddressDetails;
import dev.fincke.hopper.marketplace.service.dto.BuyerDetails;
import dev.fincke.hopper.marketplace.service.dto.ListingPublicationRequest;
import dev.fincke.hopper.marketplace.service.dto.OrderSubmissionItem;
import dev.fincke.hopper.marketplace.service.dto.OrderSubmissionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

// * Service
// Bridges domain-facing marketplace requests with connector-specific DTOs.
@Service
public class DefaultMarketplaceConnectorService implements MarketplaceConnectorService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMarketplaceConnectorService.class);

    // * Dependencies
    // Underlying connector client (stub or remote) selected via configuration.
    private final MarketplaceClient marketplaceClient;

    // * Constructors
    // Constructor injection keeps the service testable and respects Spring best practices.
    public DefaultMarketplaceConnectorService(MarketplaceClient marketplaceClient)
    {
        this.marketplaceClient = marketplaceClient;
        LOGGER.warn("Marketplace connector service is running in stub mode; eBay/TCGPlayer integrations are unfinished.");
    }

    @Override
    public ListingResult publishListing(ListingPublicationRequest request)
    {
        ListingCommand command = toListingCommand(request);
        return marketplaceClient.createListing(command);
    }

    @Override
    public ListingResult getListing(String listingId)
    {
        return marketplaceClient.getListing(listingId);
    }

    @Override
    public OrderResult submitOrder(OrderSubmissionRequest request)
    {
        OrderCommand command = toOrderCommand(request);
        return marketplaceClient.createOrder(command);
    }

    @Override
    public OrderResult getOrder(String orderId)
    {
        return marketplaceClient.getOrder(orderId);
    }

    // * Mapping Helpers
    // Converts publication requests into connector listing commands.
    private ListingCommand toListingCommand(ListingPublicationRequest request)
    {
        MoneyValue price = new MoneyValue(request.price(), request.currency());
        List<ListingMedia> media = request.mediaUrls().stream()
            .map(ListingMedia::new)
            .toList();
        return new ListingCommand(
            request.platform(),
            request.sellerAccountId(),
            request.sku(),
            request.title(),
            request.description(),
            price,
            request.quantity(),
            media
        );
    }

    // Converts order submission requests into connector order commands.
    private OrderCommand toOrderCommand(OrderSubmissionRequest request)
    {
        BuyerInfo buyerInfo = request.buyer() == null ? null : toBuyerInfo(request.buyer());
        List<OrderItemCommand> items = request.items().stream()
            .map(this::toOrderItemCommand)
            .toList();
        return new OrderCommand(
            request.platform(),
            request.sellerAccountId(),
            request.listingId(),
            request.sku(),
            buyerInfo,
            items,
            request.idempotencyKey()
        );
    }

    private BuyerInfo toBuyerInfo(BuyerDetails buyer)
    {
        AddressInfo address = toAddressInfo(buyer.address());
        return new BuyerInfo(buyer.name(), address);
    }

    private AddressInfo toAddressInfo(AddressDetails address)
    {
        return new AddressInfo(
            address.line1(),
            address.city(),
            address.region(),
            address.postal(),
            address.country()
        );
    }

    private OrderItemCommand toOrderItemCommand(OrderSubmissionItem item)
    {
        MoneyValue price = new MoneyValue(item.price(), item.currency());
        return new OrderItemCommand(item.sku(), item.quantity(), price);
    }
}
