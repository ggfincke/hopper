package dev.fincke.hopper.order.order;

import dev.fincke.hopper.order.buyer.Buyer;
import dev.fincke.hopper.order.buyer.BuyerRepository;
import dev.fincke.hopper.order.buyer.exception.BuyerNotFoundException;
import dev.fincke.hopper.order.item.OrderItem;
import dev.fincke.hopper.order.item.OrderItemRepository;
import dev.fincke.hopper.order.order.dto.OrderCreateRequest;
import dev.fincke.hopper.order.order.dto.OrderResponse;
import dev.fincke.hopper.order.order.dto.OrderUpdateRequest;
import dev.fincke.hopper.order.order.exception.DuplicateExternalOrderException;
import dev.fincke.hopper.order.order.exception.InvalidOrderStatusException;
import dev.fincke.hopper.order.order.exception.OrderNotFoundException;
import dev.fincke.hopper.order.order.exception.OrderValidationException;
import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import dev.fincke.hopper.testsupport.BuyerTestBuilder;
import dev.fincke.hopper.testsupport.OrderCreateRequestBuilder;
import dev.fincke.hopper.testsupport.OrderTestBuilder;
import dev.fincke.hopper.testsupport.OrderUpdateRequestBuilder;
import dev.fincke.hopper.testsupport.PlatformTestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Unit tests for OrderServiceImpl that exercise the most critical business paths
// Tests complex order management including validation, referential integrity, and buyer relationships
// Enables Mockito framework for dependency injection testing
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest
{
    // * Test Dependencies
    
    // Repository mock for testing order persistence and external ID validation
    @Mock
    private OrderRepository orderRepository;

    // Repository mock for platform reference validation
    @Mock
    private PlatformRepository platformRepository;

    // Repository mock for buyer reference validation and lookups
    @Mock
    private BuyerRepository buyerRepository;

    // Repository mock for testing order item relationships
    @Mock
    private OrderItemRepository orderItemRepository;

    // Service under test with dependencies injected by Mockito
    @InjectMocks
    private OrderServiceImpl orderService;

    // * Create Operation Tests

    // Tests successful order creation with complete buyer and platform relationships
    @Test
    @DisplayName("createOrder returns response with buyer details when request is valid")
    void createOrder_ReturnsResponseWithBuyer()
    {
        // Test entities with proper relationships for validation
        Platform platform = PlatformTestBuilder.platform().withName("eBay").build();
        Buyer buyer = BuyerTestBuilder.buyer().withName("Alice").build();
        UUID platformId = platform.getId();
        UUID buyerId = buyer.getId();
        String externalOrderId = "EBY-123";

        // Complete order request with all required fields and relationships
        OrderCreateRequest request = OrderCreateRequestBuilder.orderCreateRequest()
            .withPlatformId(platformId)
            .withBuyerId(buyerId)
            .withExternalOrderId(externalOrderId)
            .withStatus("pending")
            .withTotalAmount(new BigDecimal("99.99"))
            .withOrderDate(Timestamp.from(Instant.parse("2024-01-10T12:00:00Z")))
            .build();

        // Mock successful reference validations (business rules: all references must exist)
        when(platformRepository.existsById(platformId)).thenReturn(true);
        when(platformRepository.findById(platformId)).thenReturn(Optional.of(platform));
        // Mock no duplicate external order ID (business rule: external IDs unique per platform)
        when(orderRepository.findByPlatformAndExternalOrderId(platform, externalOrderId)).thenReturn(null);
        when(buyerRepository.existsById(buyerId)).thenReturn(true);
        when(buyerRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        // Mock successful persistence with ID generation (simulates database behavior)
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        OrderResponse response = orderService.createOrder(request);

        // Verify response contains all requested data with buyer relationship resolved
        assertEquals(platformId, response.platformId());
        assertEquals(externalOrderId, response.externalOrderId());
        assertEquals(buyerId, response.buyerId());
        assertEquals("Alice", response.buyerName());  // Buyer name resolved from relationship
        assertEquals(new BigDecimal("99.99"), response.totalAmount());

        // Verify order was persisted to database
        verify(orderRepository).save(any(Order.class));
    }

    // Tests enforcement of external order ID uniqueness constraint per platform
    @Test
    @DisplayName("createOrder throws DuplicateExternalOrderException when order already exists")
    void createOrder_ThrowsWhenDuplicateExternalIdExists()
    {
        // Setup test data that would create duplicate external order ID
        Platform platform = PlatformTestBuilder.platform().build();
        UUID platformId = platform.getId();
        String externalOrderId = "EBY-123";
        OrderCreateRequest request = OrderCreateRequestBuilder.orderCreateRequest()
            .withPlatformId(platformId)
            .withExternalOrderId(externalOrderId)
            .build();

        // Mock valid platform but existing external order ID (business rule violation)
        when(platformRepository.existsById(platformId)).thenReturn(true);
        when(platformRepository.findById(platformId)).thenReturn(Optional.of(platform));
        when(orderRepository.findByPlatformAndExternalOrderId(platform, externalOrderId))
            .thenReturn(OrderTestBuilder.order().withPlatform(platform).build());

        // Verify business rule: external order IDs must be unique per platform
        assertThrows(DuplicateExternalOrderException.class, () -> orderService.createOrder(request));
        // Verify no save attempt when validation fails
        verify(orderRepository, never()).save(any(Order.class));
    }

    // Tests comprehensive validation error collection and reporting
    @Test
    @DisplayName("createOrder propagates validation errors when platform or status invalid")
    void createOrder_ThrowsValidationExceptionWhenPlatformMissing()
    {
        // Request with multiple validation failures for error aggregation test
        UUID platformId = UUID.randomUUID();
        OrderCreateRequest request = OrderCreateRequestBuilder.orderCreateRequest()
            .withPlatformId(platformId)  // Non-existent platform
            .withStatus("unknown_status")  // Invalid status
            .build();

        // Mock platform not found (validation failure)
        when(platformRepository.existsById(platformId)).thenReturn(false);

        // Verify validation collects all errors in single exception
        OrderValidationException exception = assertThrows(OrderValidationException.class, () -> orderService.createOrder(request));
        assertTrue(exception.getValidationErrors().stream().anyMatch(msg -> msg.contains("Platform does not exist")));
        assertTrue(exception.getValidationErrors().stream().anyMatch(msg -> msg.contains("Invalid status")));
        // Verify no save attempt when validation fails
        verify(orderRepository, never()).save(any(Order.class));
    }

    // * Update Operation Tests

    // Tests successful update of mutable order fields with validation
    @Test
    @DisplayName("updateOrder updates mutable fields when request passes validation")
    void updateOrder_UpdatesFields()
    {
        // Existing order with initial values for update comparison
        Order existingOrder = OrderTestBuilder.order()
            .withStatus("pending")
            .build();
        UUID orderId = existingOrder.getId();
        Platform platform = existingOrder.getPlatform();
        Buyer newBuyer = BuyerTestBuilder.buyer().withName("Bob").build();
        String newExternalId = "EBY-456";

        OrderUpdateRequest request = OrderUpdateRequestBuilder.orderUpdateRequest()
            .withExternalOrderId(newExternalId)
            .withStatus("confirmed")
            .withTotalAmount(new BigDecimal("150.00"))
            .withOrderDate(Timestamp.from(Instant.parse("2024-01-11T08:30:00Z")))
            .withBuyerId(newBuyer.getId())
            .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.findByPlatformAndExternalOrderId(platform, newExternalId)).thenReturn(null);
        when(buyerRepository.existsById(newBuyer.getId())).thenReturn(true);
        when(buyerRepository.findById(newBuyer.getId())).thenReturn(Optional.of(newBuyer));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        OrderResponse response = orderService.updateOrder(orderId, request);

        assertEquals(orderId, response.id());
        assertEquals(newExternalId, response.externalOrderId());
        assertEquals("confirmed", response.status());
        assertEquals(new BigDecimal("150.00"), response.totalAmount());
        assertEquals(newBuyer.getId(), response.buyerId());
    }

    @Test
    @DisplayName("updateOrder rejects invalid status transitions")
    void updateOrder_ThrowsWhenStatusTransitionInvalid()
    {
        Order existingOrder = OrderTestBuilder.order()
            .withStatus("pending")
            .build();
        UUID orderId = existingOrder.getId();

        OrderUpdateRequest request = OrderUpdateRequestBuilder.orderUpdateRequest()
            .withStatus("shipped")
            .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateOrder(orderId, request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("calculateTotalFromItems multiplies quantity and price for each item")
    void calculateTotalFromItems_ComputesSum()
    {
        UUID orderId = UUID.randomUUID();
        OrderItem itemOne = mock(OrderItem.class);
        when(itemOne.getPrice()).thenReturn(new BigDecimal("10.00"));
        when(itemOne.getQuantity()).thenReturn(2);

        OrderItem itemTwo = mock(OrderItem.class);
        when(itemTwo.getPrice()).thenReturn(new BigDecimal("5.50"));
        when(itemTwo.getQuantity()).thenReturn(3);

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(itemOne, itemTwo));

        BigDecimal total = orderService.calculateTotalFromItems(orderId);
        assertEquals(new BigDecimal("36.50"), total);
    }

    @Test
    @DisplayName("verifyOrderTotal compares calculated total with stored value")
    void verifyOrderTotal_ReturnsTrueWhenTotalsMatch()
    {
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.order()
            .withId(orderId)
            .withTotalAmount(new BigDecimal("36.50"))
            .build();

        OrderItem firstItem = mockItem(new BigDecimal("10.00"), 2);
        OrderItem secondItem = mockItem(new BigDecimal("5.50"), 3);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(firstItem, secondItem));

        assertTrue(orderService.verifyOrderTotal(orderId));
    }

    @Test
    @DisplayName("verifyOrderTotal returns false when totals differ")
    void verifyOrderTotal_ReturnsFalseWhenTotalsDiffer()
    {
        UUID orderId = UUID.randomUUID();
        Order order = OrderTestBuilder.order()
            .withId(orderId)
            .withTotalAmount(new BigDecimal("10.00"))
            .build();

        OrderItem onlyItem = mockItem(new BigDecimal("10.00"), 2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(onlyItem));

        assertFalse(orderService.verifyOrderTotal(orderId));
    }

    @Test
    @DisplayName("assignBuyer links buyer to order and persists change")
    void assignBuyer_AssignsExistingBuyer()
    {
        Order existingOrder = OrderTestBuilder.order().withStatus("pending").build();
        UUID orderId = existingOrder.getId();
        Buyer buyer = BuyerTestBuilder.buyer().withName("Charlie").build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(buyerRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        OrderResponse response = orderService.assignBuyer(orderId, buyer.getId());
        assertEquals(buyer.getId(), response.buyerId());
    }

    @Test
    @DisplayName("assignBuyer throws when buyer missing")
    void assignBuyer_ThrowsWhenBuyerMissing()
    {
        Order existingOrder = OrderTestBuilder.order().withStatus("pending").build();
        UUID orderId = existingOrder.getId();
        UUID buyerId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(buyerRepository.findById(buyerId)).thenReturn(Optional.empty());

        assertThrows(BuyerNotFoundException.class, () -> orderService.assignBuyer(orderId, buyerId));
    }

    @Test
    @DisplayName("unassignBuyer clears buyer association")
    void unassignBuyer_RemovesBuyer()
    {
        Buyer buyer = BuyerTestBuilder.buyer().build();
        Order existingOrder = OrderTestBuilder.order().withBuyer(buyer).build();
        UUID orderId = existingOrder.getId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        OrderResponse response = orderService.unassignBuyer(orderId);
        assertNull(response.buyerId());
    }

    @Test
    @DisplayName("findById throws when order is missing")
    void findById_ThrowsWhenMissing()
    {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.findById(orderId));
    }

    private OrderItem mockItem(BigDecimal price, int quantity)
    {
        OrderItem item = mock(OrderItem.class);
        when(item.getPrice()).thenReturn(price);
        when(item.getQuantity()).thenReturn(quantity);
        return item;
    }
}
