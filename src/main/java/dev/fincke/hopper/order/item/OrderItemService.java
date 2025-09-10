package dev.fincke.hopper.order.item;

import dev.fincke.hopper.order.item.dto.OrderItemCreateRequest;
import dev.fincke.hopper.order.item.dto.OrderItemResponse;
import dev.fincke.hopper.order.item.dto.OrderItemUpdateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Service interface defining order item business operations
public interface OrderItemService
{
    
    // * Core CRUD Operations
    
    // create new order item from request data with validation
    OrderItemResponse createOrderItem(OrderItemCreateRequest request);
    
    // update existing order item by ID
    OrderItemResponse updateOrderItem(UUID id, OrderItemUpdateRequest request);
    
    // find order item by ID (throws exception if not found)
    OrderItemResponse findById(UUID id);
    
    // retrieve all order items
    List<OrderItemResponse> findAll();
    
    // delete order item by ID
    void deleteOrderItem(UUID id);
    
    // * Order-Based Operations
    
    // find all items for specific order
    List<OrderItemResponse> findByOrderId(UUID orderId);
    
    // calculate total amount for all items in an order
    BigDecimal calculateOrderTotal(UUID orderId);
    
    // count total items in an order
    int countItemsInOrder(UUID orderId);
    
    // remove all items from an order (useful for order cancellation)
    void removeAllItemsFromOrder(UUID orderId);
    
    // * Listing-Based Operations
    
    // find all order items that reference a specific listing
    List<OrderItemResponse> findByListingId(UUID listingId);
    
    // check if listing has sufficient quantity for order item
    boolean checkListingAvailability(UUID listingId, int requestedQuantity);
    
    // calculate total quantity ordered for a specific listing
    int calculateTotalOrderedQuantity(UUID listingId);
    
    // * Quantity and Price Management
    
    // update quantity for existing order item (with stock validation)
    OrderItemResponse updateQuantity(UUID id, int newQuantity);
    
    // update price for existing order item
    OrderItemResponse updatePrice(UUID id, BigDecimal newPrice);
    
    // calculate line total (quantity * price) for order item
    BigDecimal calculateLineTotal(UUID id);
    
    // sync price from current listing price
    OrderItemResponse syncPriceFromListing(UUID id);
    
    // * Validation Operations
    
    // validate order item data meets business requirements
    void validateOrderItemData(OrderItemCreateRequest request);
    
    // validate order item update data
    void validateOrderItemUpdate(UUID id, OrderItemUpdateRequest request);
    
    // validate that order item can be added to order (capacity, business rules)
    boolean canAddItemToOrder(UUID orderId, UUID listingId, int quantity);
}