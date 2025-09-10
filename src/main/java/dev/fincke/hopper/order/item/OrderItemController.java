package dev.fincke.hopper.order.item;

import dev.fincke.hopper.order.item.dto.OrderItemCreateRequest;
import dev.fincke.hopper.order.item.dto.OrderItemResponse;
import dev.fincke.hopper.order.item.dto.OrderItemUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController
{
    // * Dependencies
    // service for order item business operations
    private final OrderItemService orderItemService;

    // * Constructor
    public OrderItemController(OrderItemService orderItemService)
    {
        this.orderItemService = orderItemService;
    }

    // * Routes
    // GET /api/order-items - list all order items
    @GetMapping
    public List<OrderItemResponse> list()
    {
        return orderItemService.findAll();
    }

    // GET /api/order-items/{id} - get order item by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getById(@PathVariable String id)
    {
        try
        {
            UUID itemId = UUID.fromString(id);
            OrderItemResponse orderItem = orderItemService.findById(itemId);
            return ResponseEntity.ok(orderItem);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/order-items/order/{orderId} - get all items for an order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemResponse>> getByOrderId(@PathVariable String orderId)
    {
        try
        {
            UUID orderUuid = UUID.fromString(orderId);
            List<OrderItemResponse> items = orderItemService.findByOrderId(orderUuid);
            return ResponseEntity.ok(items);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /api/order-items - create new order item
    @PostMapping
    public ResponseEntity<OrderItemResponse> create(@Valid @RequestBody OrderItemCreateRequest request)
    {
        try
        {
            OrderItemResponse orderItem = orderItemService.createOrderItem(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderItem);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // PUT /api/order-items/{id} - update existing order item
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponse> update(@PathVariable String id, 
                                                   @Valid @RequestBody OrderItemUpdateRequest request)
    {
        try
        {
            UUID itemId = UUID.fromString(id);
            OrderItemResponse orderItem = orderItemService.updateOrderItem(itemId, request);
            return ResponseEntity.ok(orderItem);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE /api/order-items/{id} - delete order item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id)
    {
        try
        {
            UUID itemId = UUID.fromString(id);
            orderItemService.deleteOrderItem(itemId);
            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /api/order-items/{id}/line-total - calculate line total for order item
    @GetMapping("/{id}/line-total")
    public ResponseEntity<BigDecimal> getLineTotal(@PathVariable String id)
    {
        try
        {
            UUID itemId = UUID.fromString(id);
            BigDecimal lineTotal = orderItemService.calculateLineTotal(itemId);
            return ResponseEntity.ok(lineTotal);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
}