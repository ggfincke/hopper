package dev.fincke.hopper.order.order;

import dev.fincke.hopper.order.order.dto.OrderCreateRequest;
import dev.fincke.hopper.order.order.dto.OrderResponse;
import dev.fincke.hopper.order.order.dto.OrderStatusUpdateRequest;
import dev.fincke.hopper.order.order.dto.OrderUpdateRequest;
import dev.fincke.hopper.order.order.exception.OrderNotFoundException;
import dev.fincke.hopper.order.order.exception.OrderValidationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController
{
    // * Dependencies
    // service layer for order business operations
    private final OrderService orderService;

    // * Constructor
    public OrderController(OrderService orderService)
    {
        this.orderService = orderService;
    }

    // * Routes
    // GET /api/orders - list all orders
    @GetMapping
    public List<OrderResponse> list()
    {
        return orderService.findAll();
    }

    // GET /api/orders/{id} - get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable String id)
    {
        try
        {
            UUID orderId = UUID.fromString(id);
            OrderResponse order = orderService.findById(orderId);
            return ResponseEntity.ok(order);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (OrderNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/orders/status/{status} - get orders by status
    @GetMapping("/status/{status}")
    public List<OrderResponse> getByStatus(@PathVariable String status)
    {
        return orderService.findByStatus(status);
    }
    
    // POST /api/orders - create new order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request)
    {
        try
        {
            OrderResponse order = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        }
        catch (OrderValidationException e)
        {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PUT /api/orders/{id} - update existing order
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable String id, @Valid @RequestBody OrderUpdateRequest request)
    {
        try
        {
            UUID orderId = UUID.fromString(id);
            OrderResponse order = orderService.updateOrder(orderId, request);
            return ResponseEntity.ok(order);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (OrderNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (OrderValidationException e)
        {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PATCH /api/orders/{id}/status - update order status
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable String id, @Valid @RequestBody OrderStatusUpdateRequest request)
    {
        try
        {
            UUID orderId = UUID.fromString(id);
            OrderResponse order = orderService.updateStatus(orderId, request);
            return ResponseEntity.ok(order);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (OrderNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE /api/orders/{id} - delete order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id)
    {
        try
        {
            UUID orderId = UUID.fromString(id);
            orderService.deleteOrder(orderId);
            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (OrderNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /api/orders/buyer/{buyerId} - get orders for specific buyer
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByBuyer(@PathVariable String buyerId)
    {
        try
        {
            UUID id = UUID.fromString(buyerId);
            List<OrderResponse> orders = orderService.findByBuyerId(id);
            return ResponseEntity.ok(orders);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PUT /api/orders/{orderId}/buyer/{buyerId} - assign buyer to order
    @PutMapping("/{orderId}/buyer/{buyerId}")
    public ResponseEntity<OrderResponse> assignBuyer(@PathVariable String orderId, @PathVariable String buyerId)
    {
        try
        {
            UUID orderUuid = UUID.fromString(orderId);
            UUID buyerUuid = UUID.fromString(buyerId);
            OrderResponse order = orderService.assignBuyer(orderUuid, buyerUuid);
            return ResponseEntity.ok(order);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (OrderNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE /api/orders/{orderId}/buyer - remove buyer from order
    @DeleteMapping("/{orderId}/buyer")
    public ResponseEntity<OrderResponse> unassignBuyer(@PathVariable String orderId)
    {
        try
        {
            UUID orderUuid = UUID.fromString(orderId);
            OrderResponse order = orderService.unassignBuyer(orderUuid);
            return ResponseEntity.ok(order);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (OrderNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // PUT /api/orders/{id}/recalculate-total - recalculate order total from items
    @PutMapping("/{id}/recalculate-total")
    public ResponseEntity<OrderResponse> recalculateTotal(@PathVariable String id)
    {
        try
        {
            UUID orderId = UUID.fromString(id);
            OrderResponse order = orderService.recalculateTotal(orderId);
            return ResponseEntity.ok(order);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (OrderNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
}