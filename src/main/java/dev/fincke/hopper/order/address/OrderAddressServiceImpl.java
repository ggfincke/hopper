package dev.fincke.hopper.order.address;

import dev.fincke.hopper.order.address.dto.OrderAddressCreateRequest;
import dev.fincke.hopper.order.address.dto.OrderAddressResponse;
import dev.fincke.hopper.order.address.dto.OrderAddressUpdateRequest;
import dev.fincke.hopper.order.address.exception.DuplicateOrderAddressException;
import dev.fincke.hopper.order.address.exception.OrderAddressNotFoundException;
import dev.fincke.hopper.order.address.exception.OrderAddressDeletionNotAllowedException;
import dev.fincke.hopper.order.order.Order;
import dev.fincke.hopper.order.order.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Service implementation for order address business operations with transaction management
// Spring service with read-only transactions by default
@Service
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class OrderAddressServiceImpl implements OrderAddressService
{
    
    // * Dependencies
    
    // Repository for order address data access
    private final OrderAddressRepository orderAddressRepository;
    
    // Repository for order data access (needed for validation)
    private final OrderRepository orderRepository;
    
    // US postal code pattern (ZIP codes: 12345 or 12345-6789)
    private static final Pattern US_POSTAL_CODE_PATTERN = Pattern.compile("^\\d{5}(-\\d{4})?$");
    
    // * Constructor
    
    // Constructor injection for repository dependencies
    public OrderAddressServiceImpl(OrderAddressRepository orderAddressRepository, OrderRepository orderRepository)
    {
        this.orderAddressRepository = orderAddressRepository;
        this.orderRepository = orderRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional
    public OrderAddressResponse createOrderAddress(OrderAddressCreateRequest request)
    {
        
        // Validate that order exists (business rule)
        Order order = orderRepository.findById(request.orderId())
            .orElseThrow(() -> new IllegalArgumentException("Order with ID " + request.orderId() + " not found"));
        
        // Validate order doesn't already have an address (business rule)
        if (orderAddressRepository.findByOrderId(request.orderId()) != null)
        {
            throw new DuplicateOrderAddressException(request.orderId());
        }
        
        // Create and populate entity
        OrderAddress orderAddress = new OrderAddress(
            order,
            request.street(),
            request.city(), 
            request.state(),
            request.postalCode(),
            request.country()
        );
        
        // Save and return response DTO
        OrderAddress savedAddress = orderAddressRepository.save(orderAddress);
        return OrderAddressResponse.from(savedAddress);
    }
    
    @Override
    @Transactional
    public OrderAddressResponse updateOrderAddress(UUID id, OrderAddressUpdateRequest request)
    {
        
        // Find existing address
        OrderAddress orderAddress = orderAddressRepository.findById(id)
            .orElseThrow(() -> new OrderAddressNotFoundException(id));
        
        // Validate that at least one field is provided for update
        if (!request.hasUpdates())
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // Update street if provided
        if (request.street() != null)
        {
            orderAddress.setStreet(request.street());
        }
        
        // Update city if provided
        if (request.city() != null)
        {
            orderAddress.setCity(request.city());
        }
        
        // Update state if provided
        if (request.state() != null)
        {
            orderAddress.setState(request.state());
        }
        
        // Update postal code if provided (with US validation)
        if (request.postalCode() != null)
        {
            if (isValidUsPostalCode(request.postalCode()))
            {
                orderAddress.setPostalCode(request.postalCode());
            }
            else
            {
                throw new IllegalArgumentException("Invalid US postal code format");
            }
        }

        // Update country if provided
        if (request.country() != null)
        {
            orderAddress.setCountry(request.country());
        }
        
        OrderAddress savedAddress = orderAddressRepository.save(orderAddress);
        return OrderAddressResponse.from(savedAddress);
    }
    
    @Override
    public OrderAddressResponse findById(UUID id)
    {
        OrderAddress orderAddress = orderAddressRepository.findById(id)
            .orElseThrow(() -> new OrderAddressNotFoundException(id));
        return OrderAddressResponse.from(orderAddress);
    }
    
    @Override
    public List<OrderAddressResponse> findAll()
    {
        return orderAddressRepository.findAll().stream()
            .map(OrderAddressResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteOrderAddress(UUID id)
    {
        
        if (!orderAddressRepository.existsById(id))
        {
            throw new OrderAddressNotFoundException(id);
        }

        OrderAddress orderAddress = orderAddressRepository.findById(id)
            .orElseThrow(() -> new OrderAddressNotFoundException(id));

        String status = orderAddress.getOrder().getStatus();
        if (!isFinalOrderStatus(status))
        {
            throw new OrderAddressDeletionNotAllowedException(id, "associated order is still active");
        }

        orderAddressRepository.deleteById(id);
    }
    
    // * Order-Based Operations
    
    @Override
    public OrderAddressResponse findByOrderId(UUID orderId)
    {
        
        if (orderId == null)
        {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        
        OrderAddress orderAddress = orderAddressRepository.findByOrderId(orderId);
        
        if (orderAddress == null)
        {
            throw new OrderAddressNotFoundException("order", orderId);
        }
        
        return OrderAddressResponse.from(orderAddress);
    }
    
    @Override
    public boolean existsByOrderId(UUID orderId)
    {
        
        if (orderId == null)
        {
            return false;
        }
        
        return orderAddressRepository.findByOrderId(orderId) != null;
    }
    
    // * Geographic Query Operations
    
    @Override
    public List<OrderAddressResponse> findByCity(String city)
    {
        
        if (city == null || city.trim().isEmpty())
        {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        
        String searchCity = city.trim();
        return orderAddressRepository.findByCity(searchCity).stream()
            .map(OrderAddressResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderAddressResponse> findByState(String state)
    {
        
        if (state == null || state.trim().isEmpty())
        {
            throw new IllegalArgumentException("State cannot be null or empty");
        }
        
        String searchState = state.trim();
        return orderAddressRepository.findByState(searchState).stream()
            .map(OrderAddressResponse::from)
            .collect(Collectors.toList());
    }
    
    
    @Override
    public List<OrderAddressResponse> findByPostalCode(String postalCode)
    {
        
        if (postalCode == null || postalCode.trim().isEmpty())
        {
            throw new IllegalArgumentException("Postal code cannot be null or empty");
        }
        
        String searchPostalCode = postalCode.trim();
        return orderAddressRepository.findByPostalCode(searchPostalCode).stream()
            .map(OrderAddressResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Validation Operations
    
    @Override
    public void validateAddress(UUID id)
    {
        
        if (!orderAddressRepository.existsById(id))
        {
            throw new OrderAddressNotFoundException(id);
        }
        
        // Additional validation logic can be added here
        // such as checking address completeness, geocoding validation, etc.
    }
    
    @Override
    public boolean isValidUsPostalCode(String postalCode)
    {
        
        if (postalCode == null || postalCode.trim().isEmpty())
        {
            return false;
        }
        
        String cleanPostalCode = postalCode.trim();
        return US_POSTAL_CODE_PATTERN.matcher(cleanPostalCode).matches();
    }

    private boolean isFinalOrderStatus(String status)
    {
        if (status == null)
        {
            return false;
        }
        String normalizedStatus = status.trim().toLowerCase();
        return normalizedStatus.equals("delivered") || normalizedStatus.equals("cancelled") || normalizedStatus.equals("refunded");
    }

}
