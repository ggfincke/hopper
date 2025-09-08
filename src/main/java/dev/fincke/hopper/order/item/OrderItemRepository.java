package dev.fincke.hopper.order.item;

import dev.fincke.hopper.catalog.listing.Listing;
import dev.fincke.hopper.order.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// * Repository
// Data-access layer for OrderItem entities
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>
{
    // find all items for a specific order
    List<OrderItem> findByOrder(Order order);

    // find all items for a specific listing
    List<OrderItem> findByListing(Listing listing);

    // find items by order ID
    List<OrderItem> findByOrderId(UUID orderId);
}