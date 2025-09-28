package dev.fincke.hopper.marketplace.client.model;

import java.util.List;
import java.util.Objects;

// * Command DTO
// Conveys normalized order submission data to marketplace adapters.
public record OrderCommand(
    String platform,
    String sellerAccountId,
    String listingId,
    String sku,
    BuyerInfo buyer,
    List<OrderItemCommand> items,
    String idempotencyKey
)
{
    // * Canonical Constructor
    // Validates all required business fields before contacting external APIs.
    public OrderCommand
    {
        Objects.requireNonNull(platform, "platform");
        Objects.requireNonNull(sellerAccountId, "sellerAccountId");
        Objects.requireNonNull(items, "items");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey");

        platform = platform.trim().toUpperCase();
        sellerAccountId = sellerAccountId.trim();
        idempotencyKey = idempotencyKey.trim();

        if (platform.isEmpty())
        {
            throw new IllegalArgumentException("platform must not be blank");
        }
        if (sellerAccountId.isEmpty())
        {
            throw new IllegalArgumentException("sellerAccountId must not be blank");
        }
        if (idempotencyKey.isEmpty())
        {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
        if (items.isEmpty())
        {
            throw new IllegalArgumentException("items must not be empty");
        }

        if (listingId != null)
        {
            listingId = listingId.trim();
            if (listingId.isEmpty())
            {
                listingId = null;
            }
        }

        if (sku != null)
        {
            sku = sku.trim();
            if (sku.isEmpty())
            {
                sku = null;
            }
        }

        if (listingId == null && sku == null)
        {
            throw new IllegalArgumentException("either listingId or sku must be provided");
        }

        if (buyer != null && buyer.address() == null)
        {
            throw new IllegalArgumentException("buyer address must be provided when buyer is present");
        }

        items = List.copyOf(items);
    }

    // Indicates whether the connector should target an existing listing reference.
    public boolean referencesListingId()
    {
        return listingId != null;
    }
}
