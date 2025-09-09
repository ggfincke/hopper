package dev.fincke.hopper.order.address;

import dev.fincke.hopper.order.address.dto.OrderAddressCreateRequest;
import dev.fincke.hopper.order.address.dto.OrderAddressResponse;
import dev.fincke.hopper.order.address.dto.OrderAddressUpdateRequest;

import java.util.List;
import java.util.UUID;

// Service interface defining order address business operations
public interface OrderAddressService
{
    
    // * Core CRUD Operations
    
    // create new address for order from request data
    OrderAddressResponse createOrderAddress(OrderAddressCreateRequest request);
    
    // update existing address by ID
    OrderAddressResponse updateOrderAddress(UUID id, OrderAddressUpdateRequest request);
    
    // find address by ID (throws exception if not found)
    OrderAddressResponse findById(UUID id);
    
    // retrieve all addresses
    List<OrderAddressResponse> findAll();
    
    // delete address by ID
    void deleteOrderAddress(UUID id);
    
    // * Order-Based Operations
    
    // find address for specific order
    OrderAddressResponse findByOrderId(UUID orderId);
    
    // check if address exists for order
    boolean existsByOrderId(UUID orderId);
    
    // * Geographic Query Operations
    
    // find addresses by city (case-insensitive)
    List<OrderAddressResponse> findByCity(String city);
    
    // find addresses by state/province
    List<OrderAddressResponse> findByState(String state);
    
    // find addresses by postal code
    List<OrderAddressResponse> findByPostalCode(String postalCode);
    
    // * Validation Operations
    
    // validate address format and completeness
    void validateAddress(UUID id);
    
    // check if postal code format is valid for US
    boolean isValidUsPostalCode(String postalCode);
}