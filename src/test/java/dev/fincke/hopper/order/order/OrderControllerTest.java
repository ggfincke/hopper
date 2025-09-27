package dev.fincke.hopper.order.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fincke.hopper.order.order.dto.OrderCreateRequest;
import dev.fincke.hopper.order.order.dto.OrderResponse;
import dev.fincke.hopper.testsupport.BuyerTestBuilder;
import dev.fincke.hopper.testsupport.OrderCreateRequestBuilder;
import dev.fincke.hopper.testsupport.OrderTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Web MVC tests for OrderController ensuring endpoint wiring works as expected
// Tests REST API endpoints using Spring MockMvc for HTTP request/response validation
// Enables Mockito framework for dependency injection testing
@ExtendWith(MockitoExtension.class)
class OrderControllerTest
{
    // * Test Infrastructure
    
    // Spring MockMvc for simulating HTTP requests without starting full server
    private MockMvc mockMvc;

    // Jackson mapper for JSON serialization in request bodies
    private ObjectMapper objectMapper;

    // Service mock for isolating controller layer testing
    @Mock
    private OrderService orderService;

    // Controller under test with dependencies injected by Mockito
    @InjectMocks
    private OrderController orderController;

    // Setup method to initialize test infrastructure before each test
    @BeforeEach
    void setUp()
    {
        // Initialize JSON mapper for request/response serialization
        objectMapper = new ObjectMapper();
        // Configure MockMvc with controller (standalone setup for unit testing)
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    // * GET Endpoint Tests

    // Tests successful order retrieval by ID with proper JSON response mapping
    @Test
    @DisplayName("GET /api/orders/{id} returns order when found")
    void getById_ReturnsOrder()
        throws Exception
    {
        // Test order with buyer for response validation
        Order order = OrderTestBuilder.order()
            .withId(UUID.randomUUID())
            .withBuyer(BuyerTestBuilder.buyer().withName("Alice").build())
            .build();
        OrderResponse response = OrderResponse.from(order);

        // Mock service to return order when found
        when(orderService.findById(order.getId())).thenReturn(response);

        // Perform GET request and verify response
        mockMvc.perform(get("/api/orders/" + order.getId()))
            .andExpect(status().isOk())  // HTTP 200 when order found
            .andExpect(jsonPath("$.id").value(order.getId().toString()))
            .andExpect(jsonPath("$.buyerName").value("Alice"));  // Verify nested buyer data
    }

    // Tests path variable validation for malformed UUID format
    @Test
    @DisplayName("GET /api/orders/{id} returns 400 for invalid UUID")
    void getById_ReturnsBadRequestWhenUuidInvalid()
        throws Exception
    {
        // Request with invalid UUID format should trigger validation error
        mockMvc.perform(get("/api/orders/not-a-uuid"))
            .andExpect(status().isBadRequest());  // HTTP 400 for invalid path parameter
    }

    // * POST Endpoint Tests

    // Tests order creation endpoint with JSON request body and 201 response
    @Test
    @DisplayName("POST /api/orders creates order and returns 201")
    void createOrder_ReturnsCreated()
        throws Exception
    {
        // Request DTO with valid order data for creation test
        OrderCreateRequest request = OrderCreateRequestBuilder.orderCreateRequest().build();
        OrderResponse response = OrderResponse.from(
            OrderTestBuilder.order()
                .withId(UUID.randomUUID())
                .withExternalOrderId(request.externalOrderId())
                .withStatus(request.status())
                .build()
        );

        // Mock service to return created order response
        when(orderService.createOrder(any(OrderCreateRequest.class))).thenReturn(response);

        // Perform POST request with JSON body
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)  // Required for JSON request body
                .content(objectMapper.writeValueAsString(request)))  // Serialize request to JSON
            .andExpect(status().isCreated())  // HTTP 201 for successful creation
            .andExpect(jsonPath("$.externalOrderId").value(request.externalOrderId()));

        // Verify service method was called with deserialized request
        verify(orderService).createOrder(any(OrderCreateRequest.class));
    }

    // Tests list endpoint returning JSON array of all orders
    @Test
    @DisplayName("GET /api/orders returns list from service")
    void list_ReturnsAll()
        throws Exception
    {
        // Multiple order responses for list endpoint test
        OrderResponse first = OrderResponse.from(OrderTestBuilder.order().build());
        OrderResponse second = OrderResponse.from(OrderTestBuilder.order()
            .withExternalOrderId("ORD-2000")
            .build());

        // Mock service to return list of orders
        when(orderService.findAll()).thenReturn(List.of(first, second));

        // Perform GET request to list endpoint
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())  // HTTP 200 for successful list retrieval
            .andExpect(jsonPath("$", hasSize(2)));  // Verify JSON array contains 2 items
    }
}
