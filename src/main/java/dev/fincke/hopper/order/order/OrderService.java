package dev.fincke.hopper.order.order;

import dev.fincke.hopper.order.order.dto.OrderCreateRequest;
import dev.fincke.hopper.order.order.dto.OrderResponse;
import dev.fincke.hopper.order.order.dto.OrderStatusUpdateRequest;
import dev.fincke.hopper.order.order.dto.OrderUpdateRequest;
import dev.fincke.hopper.platform.platform.Platform;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

// Service interface defining order business operations
public interface OrderService
{
    
    // * Core CRUD Operations
    
    // create new order from request data with validation
    OrderResponse createOrder(OrderCreateRequest request);
    
    // update existing order by ID
    OrderResponse updateOrder(UUID id, OrderUpdateRequest request);
    
    // find order by ID (throws exception if not found)
    OrderResponse findById(UUID id);
    
    // retrieve all orders
    List<OrderResponse> findAll();
    
    // delete order by ID (soft delete recommended for audit trail)
    void deleteOrder(UUID id);
    
    // * Platform-Based Operations
    
    // find orders for specific platform
    List<OrderResponse> findByPlatform(Platform platform);
    
    // find order by platform and external order ID (unique constraint)
    OrderResponse findByPlatformAndExternalOrderId(Platform platform, String externalOrderId);
    
    // check if order exists for platform and external ID
    boolean existsByPlatformAndExternalOrderId(Platform platform, String externalOrderId);
    
    // * Status Management Operations
    
    // find orders by status (e.g., pending, paid, shipped, cancelled)
    List<OrderResponse> findByStatus(String status);
    
    // update order status with validation rules
    OrderResponse updateStatus(UUID id, OrderStatusUpdateRequest request);
    
    // validate status transition is allowed (business rule)
    boolean isValidStatusTransition(String currentStatus, String newStatus);
    
    // * Buyer Operations
    
    // find orders for specific buyer
    List<OrderResponse> findByBuyerId(UUID buyerId);
    
    // assign buyer to existing order
    OrderResponse assignBuyer(UUID orderId, UUID buyerId);
    
    // remove buyer assignment from order
    OrderResponse unassignBuyer(UUID orderId);
    
    // * Date Range Operations
    
    // find orders within date range
    List<OrderResponse> findByDateRange(Timestamp startDate, Timestamp endDate);
    
    // find orders by date and status
    List<OrderResponse> findByDateRangeAndStatus(Timestamp startDate, Timestamp endDate, String status);
    
    // * Total Amount Operations
    
    // calculate total amount from order items (recalculation)
    BigDecimal calculateTotalFromItems(UUID orderId);
    
    // verify order total matches sum of items
    boolean verifyOrderTotal(UUID orderId);
    
    // update order total based on items
    OrderResponse recalculateTotal(UUID orderId);
    
    // * Validation Operations
    
    // validate order data meets business requirements
    void validateOrderData(OrderCreateRequest request);
    
    // validate order update data
    void validateOrderUpdate(UUID orderId, OrderUpdateRequest request);
}