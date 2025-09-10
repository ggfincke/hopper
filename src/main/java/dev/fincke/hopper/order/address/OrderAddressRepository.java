package dev.fincke.hopper.order.address;

import dev.fincke.hopper.order.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// * Repository  
// Data-access layer for OrderAddress entities (Spring Data JPA)
public interface OrderAddressRepository extends JpaRepository<OrderAddress, UUID>
{
    // find address for a specific order (returns null if not found)
    OrderAddress findByOrder(Order order);

    // find address by order ID (returns null if not found)
    OrderAddress findByOrderId(UUID orderId);

    // find addresses by city (case-sensitive match)
    List<OrderAddress> findByCity(String city);

    // find addresses by state/province
    List<OrderAddress> findByState(String state);

    // find addresses by postal code (exact match)
    List<OrderAddress> findByPostalCode(String postalCode);

    // check if address exists for order
    boolean existsByOrderId(UUID orderId);
}