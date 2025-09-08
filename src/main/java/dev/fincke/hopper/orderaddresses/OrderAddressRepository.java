package dev.fincke.hopper.orderaddresses;

import dev.fincke.hopper.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// * Repository
// Data-access layer for OrderAddress entities
public interface OrderAddressRepository extends JpaRepository<OrderAddress, UUID>
{
    // find address for a specific order
    OrderAddress findByOrder(Order order);

    // find address by order ID
    OrderAddress findByOrderId(UUID orderId);

    // find addresses by city
    List<OrderAddress> findByCity(String city);

    // find addresses by state
    List<OrderAddress> findByState(String state);

    // find addresses by country
    List<OrderAddress> findByCountry(String country);

    // find addresses by postal code
    List<OrderAddress> findByPostalCode(String postalCode);
}