package dev.fincke.hopper.order.order;

import dev.fincke.hopper.order.buyer.Buyer;
import dev.fincke.hopper.order.buyer.BuyerRepository;
import dev.fincke.hopper.order.buyer.exception.BuyerNotFoundException;
import dev.fincke.hopper.order.item.OrderItem;
import dev.fincke.hopper.order.item.OrderItemRepository;
import dev.fincke.hopper.order.order.dto.*;
import dev.fincke.hopper.order.order.exception.*;
import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

// Service implementation for order business operations with transaction management
// Spring service with read-only transactions by default
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService
{
    
    // * Dependencies
    
    // Repository for order data access
    private final OrderRepository orderRepository;
    
    // Repository for platform data access
    private final PlatformRepository platformRepository;
    
    // Repository for buyer data access
    private final BuyerRepository buyerRepository;
    
    // Repository for order items (for total calculations)
    private final OrderItemRepository orderItemRepository;
    
    // Valid status transitions map (business rules)
    private static final Map<String, Set<String>> VALID_STATUS_TRANSITIONS = Map.of(
        "pending", Set.of("confirmed", "cancelled"),
        "confirmed", Set.of("paid", "cancelled"),
        "paid", Set.of("processing", "refunded"),
        "processing", Set.of("shipped", "cancelled"),
        "shipped", Set.of("delivered"),
        "delivered", Set.of("refunded"),
        "cancelled", Set.of(),
        "refunded", Set.of()
    );
    
    // * Constructor
    
    // Constructor injection for repository dependencies
    public OrderServiceImpl(
        OrderRepository orderRepository,
        PlatformRepository platformRepository,
        BuyerRepository buyerRepository,
        OrderItemRepository orderItemRepository)
    {
        this.orderRepository = orderRepository;
        this.platformRepository = platformRepository;
        this.buyerRepository = buyerRepository;
        this.orderItemRepository = orderItemRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request)
    {
        
        // Validate request data
        validateOrderData(request);
        
        // Fetch platform entity
        Platform platform = platformRepository.findById(request.platformId())
            .orElseThrow(() -> new OrderValidationException("platform", "Platform not found: " + request.platformId()));
        
        // Check for duplicate external order ID
        if (orderRepository.findByPlatformAndExternalOrderId(platform, request.externalOrderId()) != null)
        {
            throw new DuplicateExternalOrderException(request.platformId(), request.externalOrderId());
        }
        
        // Create order entity
        Order order = new Order(
            platform,
            request.externalOrderId(),
            request.status(),
            request.totalAmount(),
            request.orderDate()
        );
        
        // Assign buyer if provided
        if (request.hasBuyer())
        {
            Buyer buyer = buyerRepository.findById(request.buyerId())
                .orElseThrow(() -> new BuyerNotFoundException(request.buyerId()));
            order.setBuyer(buyer);
        }
        
        // Save and return response DTO
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderResponse updateOrder(UUID id, OrderUpdateRequest request)
    {
        
        // Find existing order
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        
        // Validate that at least one field is provided for update
        if (!request.hasUpdates())
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // Validate update data
        validateOrderUpdate(id, request);
        
        // Update external order ID if provided
        if (request.hasExternalOrderId())
        {
            // Check for duplicate external order ID
            Order existingOrder = orderRepository.findByPlatformAndExternalOrderId(
                order.getPlatform(), request.externalOrderId());
            if (existingOrder != null && !existingOrder.getId().equals(id))
            {
                throw new DuplicateExternalOrderException(
                    order.getPlatform().getId(), request.externalOrderId(), existingOrder.getId());
            }
            order.setExternalOrderId(request.externalOrderId());
        }
        
        // Update status if provided (with validation)
        if (request.hasStatus())
        {
            if (!isValidStatusTransition(order.getStatus(), request.status()))
            {
                throw new InvalidOrderStatusException(id, order.getStatus(), request.status());
            }
            order.setStatus(request.status());
        }
        
        // Update total amount if provided
        if (request.hasTotalAmount())
        {
            order.setTotalAmount(request.totalAmount());
        }
        
        // Update order date if provided
        if (request.hasOrderDate())
        {
            order.setOrderDate(request.orderDate());
        }
        
        // Update buyer assignment if provided
        if (request.hasBuyerUpdate())
        {
            if (request.buyerId() != null)
            {
                Buyer buyer = buyerRepository.findById(request.buyerId())
                    .orElseThrow(() -> new BuyerNotFoundException(request.buyerId()));
                order.setBuyer(buyer);
            }
            else
            {
                order.setBuyer(null);
            }
        }
        
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }
    
    @Override
    public OrderResponse findById(UUID id)
    {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderResponse.from(order);
    }
    
    @Override
    public List<OrderResponse> findAll()
    {
        return orderRepository.findAll().stream()
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteOrder(UUID id)
    {
        if (!orderRepository.existsById(id))
        {
            throw new OrderNotFoundException(id);
        }
        
        // Note: Consider soft delete for audit trail in production
        orderRepository.deleteById(id);
    }
    
    // * Platform-Based Operations
    
    @Override
    public List<OrderResponse> findByPlatform(Platform platform)
    {
        return orderRepository.findByPlatform(platform).stream()
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public OrderResponse findByPlatformAndExternalOrderId(Platform platform, String externalOrderId)
    {
        Order order = orderRepository.findByPlatformAndExternalOrderId(platform, externalOrderId);
        if (order == null)
        {
            throw new OrderNotFoundException(platform.getId(), externalOrderId);
        }
        return OrderResponse.from(order);
    }
    
    @Override
    public boolean existsByPlatformAndExternalOrderId(Platform platform, String externalOrderId)
    {
        return orderRepository.findByPlatformAndExternalOrderId(platform, externalOrderId) != null;
    }
    
    // * Status Management Operations
    
    @Override
    public List<OrderResponse> findByStatus(String status)
    {
        return orderRepository.findByStatus(status).stream()
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public OrderResponse updateStatus(UUID id, OrderStatusUpdateRequest request)
    {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        
        // Validate status transition
        if (!isValidStatusTransition(order.getStatus(), request.status()))
        {
            throw new InvalidOrderStatusException(id, order.getStatus(), request.status());
        }
        
        // Update status
        order.setStatus(request.status());
        
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }
    
    @Override
    public boolean isValidStatusTransition(String currentStatus, String newStatus)
    {
        if (currentStatus == null || newStatus == null)
        {
            return false;
        }
        
        Set<String> validTransitions = VALID_STATUS_TRANSITIONS.get(currentStatus);
        return validTransitions != null && validTransitions.contains(newStatus);
    }
    
    // * Buyer Operations
    
    @Override
    public List<OrderResponse> findByBuyerId(UUID buyerId)
    {
        // Find buyer first to ensure it exists
        buyerRepository.findById(buyerId)
            .orElseThrow(() -> new BuyerNotFoundException(buyerId));
        
        return orderRepository.findAll().stream()
            .filter(order -> order.getBuyer() != null && order.getBuyer().getId().equals(buyerId))
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public OrderResponse assignBuyer(UUID orderId, UUID buyerId)
    {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        Buyer buyer = buyerRepository.findById(buyerId)
            .orElseThrow(() -> new BuyerNotFoundException(buyerId));
        
        order.setBuyer(buyer);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderResponse unassignBuyer(UUID orderId)
    {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.setBuyer(null);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }
    
    // * Date Range Operations
    
    @Override
    public List<OrderResponse> findByDateRange(Timestamp startDate, Timestamp endDate)
    {
        return orderRepository.findAll().stream()
            .filter(order ->
            {
                Timestamp orderDate = order.getOrderDate();
                return orderDate.compareTo(startDate) >= 0 && orderDate.compareTo(endDate) <= 0;
            })
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderResponse> findByDateRangeAndStatus(Timestamp startDate, Timestamp endDate, String status)
    {
        return orderRepository.findByStatus(status).stream()
            .filter(order ->
            {
                Timestamp orderDate = order.getOrderDate();
                return orderDate.compareTo(startDate) >= 0 && orderDate.compareTo(endDate) <= 0;
            })
            .map(OrderResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Total Amount Operations
    
    @Override
    public BigDecimal calculateTotalFromItems(UUID orderId)
    {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public boolean verifyOrderTotal(UUID orderId)
    {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        BigDecimal calculatedTotal = calculateTotalFromItems(orderId);
        return order.getTotalAmount().compareTo(calculatedTotal) == 0;
    }
    
    @Override
    @Transactional
    public OrderResponse recalculateTotal(UUID orderId)
    {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        BigDecimal calculatedTotal = calculateTotalFromItems(orderId);
        order.setTotalAmount(calculatedTotal);
        
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }
    
    // * Validation Operations
    
    @Override
    public void validateOrderData(OrderCreateRequest request)
    {
        List<String> errors = new ArrayList<>();
        
        // Validate basic request data
        if (!request.isValid())
        {
            errors.add("Request data is incomplete or invalid");
        }
        
        // Validate platform exists
        if (request.platformId() != null && !platformRepository.existsById(request.platformId()))
        {
            errors.add("Platform does not exist: " + request.platformId());
        }
        
        // Validate buyer exists if provided
        if (request.hasBuyer() && !buyerRepository.existsById(request.buyerId()))
        {
            errors.add("Buyer does not exist: " + request.buyerId());
        }
        
        // Validate status is known
        if (request.status() != null && !VALID_STATUS_TRANSITIONS.containsKey(request.status()))
        {
            errors.add("Invalid status: " + request.status());
        }
        
        if (!errors.isEmpty())
        {
            throw new OrderValidationException(errors);
        }
    }
    
    @Override
    public void validateOrderUpdate(UUID orderId, OrderUpdateRequest request)
    {
        List<String> errors = new ArrayList<>();
        
        // Validate status if provided
        if (request.hasStatus() && !VALID_STATUS_TRANSITIONS.containsKey(request.status()))
        {
            errors.add("Invalid status: " + request.status());
        }
        
        // Validate buyer exists if provided
        if (request.hasBuyerUpdate() && request.buyerId() != null && !buyerRepository.existsById(request.buyerId()))
        {
            errors.add("Buyer does not exist: " + request.buyerId());
        }
        
        if (!errors.isEmpty())
        {
            throw new OrderValidationException(orderId, errors);
        }
    }
}