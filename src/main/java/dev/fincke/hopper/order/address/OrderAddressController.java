package dev.fincke.hopper.order.address;

import dev.fincke.hopper.order.address.dto.OrderAddressCreateRequest;
import dev.fincke.hopper.order.address.dto.OrderAddressResponse;
import dev.fincke.hopper.order.address.dto.OrderAddressUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// REST controller handling order address API endpoints
@RestController
@RequestMapping("/api/order-addresses")
public class OrderAddressController
{
    
    // * Dependencies
    
    // Spring will inject service dependency
    private final OrderAddressService orderAddressService;
    
    // * Constructor
    
    // Constructor injection for OrderAddressService
    public OrderAddressController(OrderAddressService orderAddressService)
    {
        this.orderAddressService = orderAddressService;
    }

    // * Core CRUD Endpoints
    
    // POST /api/order-addresses - create new address
    @PostMapping
    public ResponseEntity<OrderAddressResponse> createOrderAddress(@Valid @RequestBody OrderAddressCreateRequest request)
    {
        OrderAddressResponse response = orderAddressService.createOrderAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // PUT /api/order-addresses/{id} - update existing address
    @PutMapping("/{id}")
    public OrderAddressResponse updateOrderAddress(@PathVariable UUID id, @Valid @RequestBody OrderAddressUpdateRequest request)
    {
        return orderAddressService.updateOrderAddress(id, request);
    }
    
    // GET /api/order-addresses - list all order addresses
    @GetMapping
    public List<OrderAddressResponse> getAllOrderAddresses()
    {
        return orderAddressService.findAll();
    }

    // GET /api/order-addresses/{id} - get order address by ID
    @GetMapping("/{id}")
    public OrderAddressResponse getById(@PathVariable UUID id)
    {
        return orderAddressService.findById(id);
    }
    
    // DELETE /api/order-addresses/{id} - delete address
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderAddress(@PathVariable UUID id)
    {
        orderAddressService.deleteOrderAddress(id);
        return ResponseEntity.noContent().build();
    }

    // * Query Endpoints
    
    // GET /api/order-addresses/order/{orderId} - get address for an order
    @GetMapping("/order/{orderId}")
    public OrderAddressResponse getByOrderId(@PathVariable UUID orderId)
    {
        return orderAddressService.findByOrderId(orderId);
    }

    // GET /api/order-addresses/city/{city} - get addresses by city
    @GetMapping("/city/{city}")
    public List<OrderAddressResponse> getByCity(@PathVariable String city)
    {
        return orderAddressService.findByCity(city);
    }

    // GET /api/order-addresses/state/{state} - get addresses by state
    @GetMapping("/state/{state}")
    public List<OrderAddressResponse> getByState(@PathVariable String state)
    {
        return orderAddressService.findByState(state);
    }
    
    
    // GET /api/order-addresses/postal/{postalCode} - get addresses by postal code
    @GetMapping("/postal/{postalCode}")
    public List<OrderAddressResponse> getByPostalCode(@PathVariable String postalCode)
    {
        return orderAddressService.findByPostalCode(postalCode);
    }
    
    // * Utility Endpoints
    
    // GET /api/order-addresses/exists/order/{orderId} - check if address exists for order
    @GetMapping("/exists/order/{orderId}")
    public ResponseEntity<Boolean> existsByOrderId(@PathVariable UUID orderId)
    {
        boolean exists = orderAddressService.existsByOrderId(orderId);
        return ResponseEntity.ok(exists);
    }
    
    // POST /api/order-addresses/{id}/validate - validate address data
    @PostMapping("/{id}/validate")
    public ResponseEntity<Void> validateAddress(@PathVariable UUID id)
    {
        orderAddressService.validateAddress(id);
        return ResponseEntity.ok().build();
    }
}