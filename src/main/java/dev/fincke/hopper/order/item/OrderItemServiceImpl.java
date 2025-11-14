package dev.fincke.hopper.order.item;

import dev.fincke.hopper.catalog.listing.Listing;
import dev.fincke.hopper.catalog.listing.ListingRepository;
import dev.fincke.hopper.catalog.listing.exception.ListingNotFoundException;
import dev.fincke.hopper.order.item.dto.OrderItemCreateRequest;
import dev.fincke.hopper.order.item.dto.OrderItemResponse;
import dev.fincke.hopper.order.item.dto.OrderItemUpdateRequest;
import dev.fincke.hopper.order.item.exception.OrderItemNotFoundException;
import dev.fincke.hopper.order.order.Order;
import dev.fincke.hopper.order.order.OrderRepository;
import dev.fincke.hopper.order.order.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service implementation for order item business operations with transaction management
// Spring service with read-only transactions by default
@Service
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class OrderItemServiceImpl implements OrderItemService
{
    
    // * Dependencies
    
    // Repository for order item data access
    private final OrderItemRepository orderItemRepository;
    
    // Repository for order data access
    private final OrderRepository orderRepository;
    
    // Repository for listing data access
    private final ListingRepository listingRepository;
    
    // * Constructor
    
    // Constructor injection for repository dependencies
    public OrderItemServiceImpl(
        OrderItemRepository orderItemRepository,
        OrderRepository orderRepository,
        ListingRepository listingRepository)
    {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.listingRepository = listingRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional
    public OrderItemResponse createOrderItem(OrderItemCreateRequest request)
    {
        
        // Validate request data
        validateOrderItemData(request);
        
        // Fetch order entity
        Order order = orderRepository.findById(request.orderId())
            .orElseThrow(() -> new OrderNotFoundException(request.orderId()));
        
        // Fetch listing entity
        Listing listing = listingRepository.findById(request.listingId())
            .orElseThrow(() -> new ListingNotFoundException(request.listingId()));
        
        // Check if listing has sufficient quantity
        if (!checkListingAvailability(request.listingId(), request.quantity()))
        {
            throw new IllegalArgumentException(
                "Insufficient quantity available for listing: " + request.listingId() + 
                ". Requested: " + request.quantity() + 
                ", Available: " + listing.getQuantityListed());
        }
        
        // Create order item entity
        OrderItem orderItem = new OrderItem(
            order,
            listing,
            request.quantity(),
            request.price()
        );
        
        // Save and return response DTO
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return OrderItemResponse.from(savedOrderItem);
    }
    
    @Override
    @Transactional
    public OrderItemResponse updateOrderItem(UUID id, OrderItemUpdateRequest request)
    {
        
        // Find existing order item
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException(id));
        
        // Validate that at least one field is provided for update
        if (!request.hasUpdates())
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // Validate update data
        validateOrderItemUpdate(id, request);
        
        // Update quantity if provided (with stock validation)
        if (request.hasQuantity())
        {
            if (!checkListingAvailability(orderItem.getListing().getId(), request.quantity()))
            {
                throw new IllegalArgumentException(
                    "Insufficient quantity available for listing: " + orderItem.getListing().getId() + 
                    ". Requested: " + request.quantity() + 
                    ", Available: " + orderItem.getListing().getQuantityListed());
            }
            orderItem.setQuantity(request.quantity());
        }
        
        // Update price if provided
        if (request.hasPrice())
        {
            orderItem.setPrice(request.price());
        }
        
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return OrderItemResponse.from(savedOrderItem);
    }
    
    @Override
    public OrderItemResponse findById(UUID id)
    {
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException(id));
        return OrderItemResponse.from(orderItem);
    }
    
    @Override
    public List<OrderItemResponse> findAll()
    {
        return orderItemRepository.findAll().stream()
            .map(OrderItemResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteOrderItem(UUID id)
    {
        if (!orderItemRepository.existsById(id))
        {
            throw new OrderItemNotFoundException(id);
        }
        
        orderItemRepository.deleteById(id);
    }
    
    // * Order-Based Operations
    
    @Override
    public List<OrderItemResponse> findByOrderId(UUID orderId)
    {
        // Verify order exists
        orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        return orderItemRepository.findByOrderId(orderId).stream()
            .map(OrderItemResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public BigDecimal calculateOrderTotal(UUID orderId)
    {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public int countItemsInOrder(UUID orderId)
    {
        return orderItemRepository.findByOrderId(orderId).stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
    }
    
    @Override
    @Transactional
    public void removeAllItemsFromOrder(UUID orderId)
    {
        // Verify order exists
        orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        orderItemRepository.deleteAll(items);
    }
    
    // * Listing-Based Operations
    
    @Override
    public List<OrderItemResponse> findByListingId(UUID listingId)
    {
        // Verify listing exists
        listingRepository.findById(listingId)
            .orElseThrow(() -> new ListingNotFoundException(listingId));
        
        Listing listing = listingRepository.findById(listingId).get();
        return orderItemRepository.findByListing(listing).stream()
            .map(OrderItemResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean checkListingAvailability(UUID listingId, int requestedQuantity)
    {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new ListingNotFoundException(listingId));
        
        // Check if listing status is active
        if (!"active".equalsIgnoreCase(listing.getStatus()))
        {
            return false;
        }
        
        // Check if sufficient quantity is available
        return listing.getQuantityListed() >= requestedQuantity;
    }
    
    @Override
    public int calculateTotalOrderedQuantity(UUID listingId)
    {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new ListingNotFoundException(listingId));
        
        return orderItemRepository.findByListing(listing).stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
    }
    
    // * Quantity and Price Management
    
    @Override
    @Transactional
    public OrderItemResponse updateQuantity(UUID id, int newQuantity)
    {
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException(id));
        
        // Validate quantity
        if (newQuantity < 1)
        {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        
        // Check availability
        if (!checkListingAvailability(orderItem.getListing().getId(), newQuantity))
        {
            throw new IllegalArgumentException(
                "Insufficient quantity available for listing: " + orderItem.getListing().getId());
        }
        
        orderItem.setQuantity(newQuantity);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return OrderItemResponse.from(savedOrderItem);
    }
    
    @Override
    @Transactional
    public OrderItemResponse updatePrice(UUID id, BigDecimal newPrice)
    {
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException(id));
        
        // Validate price
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        
        orderItem.setPrice(newPrice);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return OrderItemResponse.from(savedOrderItem);
    }
    
    @Override
    public BigDecimal calculateLineTotal(UUID id)
    {
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException(id));
        
        return orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }
    
    @Override
    @Transactional
    public OrderItemResponse syncPriceFromListing(UUID id)
    {
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException(id));
        
        // Update price to current listing price
        BigDecimal currentListingPrice = orderItem.getListing().getPrice();
        orderItem.setPrice(currentListingPrice);
        
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return OrderItemResponse.from(savedOrderItem);
    }
    
    // * Validation Operations
    
    @Override
    public void validateOrderItemData(OrderItemCreateRequest request)
    {
        List<String> errors = new ArrayList<>();
        
        // Validate basic request data
        if (!request.isValid())
        {
            errors.add("Request data is incomplete or invalid");
        }
        
        // Validate order exists
        if (request.orderId() != null && !orderRepository.existsById(request.orderId()))
        {
            errors.add("Order does not exist: " + request.orderId());
        }
        
        // Validate listing exists
        if (request.listingId() != null && !listingRepository.existsById(request.listingId()))
        {
            errors.add("Listing does not exist: " + request.listingId());
        }
        
        if (!errors.isEmpty())
        {
            throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
        }
    }
    
    @Override
    public void validateOrderItemUpdate(UUID id, OrderItemUpdateRequest request)
    {
        List<String> errors = new ArrayList<>();
        
        // Validate request has updates
        if (!request.hasUpdates())
        {
            errors.add("At least one field must be provided for update");
        }
        
        // Validate update data
        if (!request.isValid())
        {
            errors.add("Update data is invalid");
        }
        
        if (!errors.isEmpty())
        {
            throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
        }
    }
    
    @Override
    public boolean canAddItemToOrder(UUID orderId, UUID listingId, int quantity)
    {
        // Check if order exists
        if (!orderRepository.existsById(orderId))
        {
            return false;
        }
        
        // Check if listing exists and is available
        return checkListingAvailability(listingId, quantity);
    }
}
