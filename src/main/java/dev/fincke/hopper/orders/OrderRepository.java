package dev.fincke.hopper.orders;

import dev.fincke.hopper.platforms.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// * Repository
// Data-access layer for Order entities
public interface OrderRepository extends JpaRepository<Order, UUID>
{
    // find orders by platform
    List<Order> findByPlatform(Platform platform);

    // find order by platform and external order ID
    Order findByPlatformAndExternalOrderId(Platform platform, String externalOrderId);

    // find orders by status
    List<Order> findByStatus(String status);
}