package dev.fincke.hopper.order.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController
{
    // * Dependencies
    // repository to access order items
    private final OrderItemRepository repo;

    // * Constructor
    public OrderItemController(OrderItemRepository repo)
    {
        this.repo = repo;
    }

    // * Routes
    // GET /api/order-items - list all order items
    @GetMapping
    public List<OrderItemDto> list()
    {
        return repo.findAll().stream()
                .map(oi -> new OrderItemDto(
                        oi.getId().toString(),
                        oi.getOrder().getId().toString(),
                        oi.getListing().getId().toString(),
                        oi.getListing().getExternalListingId(),
                        oi.getQuantity(),
                        oi.getPrice()))
                .collect(Collectors.toList());
    }

    // GET /api/order-items/{id} - get order item by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getById(@PathVariable String id)
    {
        try {
            UUID itemId = UUID.fromString(id);
            Optional<OrderItem> orderItem = repo.findById(itemId);
            
            if (orderItem.isPresent()) {
                OrderItem oi = orderItem.get();
                OrderItemDto dto = new OrderItemDto(
                        oi.getId().toString(),
                        oi.getOrder().getId().toString(),
                        oi.getListing().getId().toString(),
                        oi.getListing().getExternalListingId(),
                        oi.getQuantity(),
                        oi.getPrice());
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/order-items/order/{orderId} - get all items for an order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemDto>> getByOrderId(@PathVariable String orderId)
    {
        try {
            UUID orderUuid = UUID.fromString(orderId);
            List<OrderItem> items = repo.findByOrderId(orderUuid);
            
            List<OrderItemDto> dtos = items.stream()
                    .map(oi -> new OrderItemDto(
                            oi.getId().toString(),
                            oi.getOrder().getId().toString(),
                            oi.getListing().getId().toString(),
                            oi.getListing().getExternalListingId(),
                            oi.getQuantity(),
                            oi.getPrice()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // * DTO
    // Represents an order item in API responses
    static class OrderItemDto
    {
        // order item ID
        private final String id;
        // order ID
        private final String orderId;
        // listing ID
        private final String listingId;
        // external listing ID for reference
        private final String externalListingId;
        // quantity purchased
        private final int quantity;
        // unit price at time of sale
        private final BigDecimal price;

        // * Constructor
        public OrderItemDto(String id, String orderId, String listingId, String externalListingId,
                           int quantity, BigDecimal price)
        {
            this.id = id;
            this.orderId = orderId;
            this.listingId = listingId;
            this.externalListingId = externalListingId;
            this.quantity = quantity;
            this.price = price;
        }

        // * Getters (for serialization)
        // order item ID
        public String getId()
        {
            return id;
        }

        // order ID
        public String getOrderId()
        {
            return orderId;
        }

        // listing ID
        public String getListingId()
        {
            return listingId;
        }

        // external listing ID
        public String getExternalListingId()
        {
            return externalListingId;
        }

        // quantity
        public int getQuantity()
        {
            return quantity;
        }

        // price
        public BigDecimal getPrice()
        {
            return price;
        }
    }
}