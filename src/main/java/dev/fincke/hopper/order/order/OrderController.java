package dev.fincke.hopper.order.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController
{
    // * Dependencies
    // repository to access orders
    private final OrderRepository repo;

    // * Constructor
    public OrderController(OrderRepository repo)
    {
        this.repo = repo;
    }

    // * Routes
    // GET /api/orders - list all orders
    @GetMapping
    public List<OrderDto> list()
    {
        return repo.findAll().stream()
                .map(o -> new OrderDto(
                        o.getId().toString(),
                        o.getPlatform().getId().toString(),
                        o.getPlatform().getName(),
                        o.getExternalOrderId(),
                        o.getStatus(),
                        o.getTotalAmount(),
                        o.getOrderDate()))
                .collect(Collectors.toList());
    }

    // GET /api/orders/{id} - get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable String id)
    {
        try {
            UUID orderId = UUID.fromString(id);
            Optional<Order> order = repo.findById(orderId);
            
            if (order.isPresent()) {
                Order o = order.get();
                OrderDto dto = new OrderDto(
                        o.getId().toString(),
                        o.getPlatform().getId().toString(),
                        o.getPlatform().getName(),
                        o.getExternalOrderId(),
                        o.getStatus(),
                        o.getTotalAmount(),
                        o.getOrderDate());
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/orders/status/{status} - get orders by status
    @GetMapping("/status/{status}")
    public List<OrderDto> getByStatus(@PathVariable String status)
    {
        return repo.findByStatus(status).stream()
                .map(o -> new OrderDto(
                        o.getId().toString(),
                        o.getPlatform().getId().toString(),
                        o.getPlatform().getName(),
                        o.getExternalOrderId(),
                        o.getStatus(),
                        o.getTotalAmount(),
                        o.getOrderDate()))
                .collect(Collectors.toList());
    }

    // * DTO
    // Represents an order in API responses
    static class OrderDto
    {
        // order ID
        private final String id;
        // platform ID
        private final String platformId;
        // platform name
        private final String platformName;
        // external order ID
        private final String externalOrderId;
        // status of the order
        private final String status;
        // total amount
        private final BigDecimal totalAmount;
        // order date
        private final Timestamp orderDate;

        // * Constructor
        public OrderDto(String id, String platformId, String platformName, String externalOrderId, 
                       String status, BigDecimal totalAmount, Timestamp orderDate)
        {
            this.id = id;
            this.platformId = platformId;
            this.platformName = platformName;
            this.externalOrderId = externalOrderId;
            this.status = status;
            this.totalAmount = totalAmount;
            this.orderDate = orderDate;
        }

        // * Getters (for serialization)
        // order ID
        public String getId()
        {
            return id;
        }

        // platform ID
        public String getPlatformId()
        {
            return platformId;
        }

        // platform name
        public String getPlatformName()
        {
            return platformName;
        }

        // external order ID
        public String getExternalOrderId()
        {
            return externalOrderId;
        }

        // status
        public String getStatus()
        {
            return status;
        }

        // total amount
        public BigDecimal getTotalAmount()
        {
            return totalAmount;
        }

        // order date
        public Timestamp getOrderDate()
        {
            return orderDate;
        }
    }
}