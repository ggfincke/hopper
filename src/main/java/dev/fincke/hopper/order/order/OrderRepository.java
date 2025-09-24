package dev.fincke.hopper.order.order;

import dev.fincke.hopper.platform.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// * Repository
// Data-access layer for Order entities
public interface OrderRepository extends JpaRepository<Order, UUID>
{
    // find orders by platform
    List<Order> findByPlatform(Platform platform);

    // find order by platform and external order ID (unique combination)
    Order findByPlatformAndExternalOrderId(Platform platform, String externalOrderId);

    // find orders by status
    List<Order> findByStatus(String status);

    // check if any orders exist for given buyer ID
    boolean existsByBuyerId(UUID buyerId);

    // check if any orders exist for given platform ID
    boolean existsByPlatformId(UUID platformId);
}
