package dev.fincke.hopper.platform.fee;

import dev.fincke.hopper.order.order.Order;
import dev.fincke.hopper.order.order.OrderRepository;
import dev.fincke.hopper.order.order.exception.OrderNotFoundException;
import dev.fincke.hopper.platform.fee.dto.PlatformFeeCreateRequest;
import dev.fincke.hopper.platform.fee.dto.PlatformFeeResponse;
import dev.fincke.hopper.platform.fee.dto.PlatformFeeUpdateRequest;
import dev.fincke.hopper.platform.fee.exception.DuplicateFeeTypeException;
import dev.fincke.hopper.platform.fee.exception.InvalidFeeAmountException;
import dev.fincke.hopper.platform.fee.exception.PlatformFeeNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Service implementation for platform fee business operations with transaction management
// Spring service with read-only transactions by default
@Service
@Transactional(readOnly = true)
public class PlatformFeeServiceImpl implements PlatformFeeService
{
    
    // * Dependencies
    
    // Repository for platform fee data access
    private final PlatformFeeRepository platformFeeRepository;
    
    // Repository for order data access
    private final OrderRepository orderRepository;
    
    // * Configuration Constants
    
    // Valid fee types accepted by the system
    private static final Set<String> VALID_FEE_TYPES = Set.of(
        "transaction", "processing", "listing", "final_value"
    );
    
    // Maximum fee amount allowed by business rules
    private static final BigDecimal MAX_FEE_AMOUNT = new BigDecimal("10000.00");
    
    // Minimum fee amount allowed by business rules  
    private static final BigDecimal MIN_FEE_AMOUNT = BigDecimal.ZERO;
    
    // * Constructor
    
    // Constructor injection for repository dependencies
    public PlatformFeeServiceImpl(
        PlatformFeeRepository platformFeeRepository,
        OrderRepository orderRepository)
    {
        this.platformFeeRepository = platformFeeRepository;
        this.orderRepository = orderRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional
    public PlatformFeeResponse createPlatformFee(PlatformFeeCreateRequest request)
    {
        
        // Validate request data
        validatePlatformFeeData(request);
        
        // Check for duplicate fee type (business rule)
        if (isDuplicateFeeType(request.orderId(), request.feeType()))
        {
            throw new DuplicateFeeTypeException(request.orderId(), request.feeType());
        }
        
        // Fetch order entity
        Order order = orderRepository.findById(request.orderId())
            .orElseThrow(() -> new OrderNotFoundException(request.orderId()));
        
        // Create platform fee entity
        PlatformFee platformFee = new PlatformFee(
            order,
            request.trimmedFeeType(),
            request.amount()
        );
        
        // Save and return response DTO
        PlatformFee savedPlatformFee = platformFeeRepository.save(platformFee);
        return PlatformFeeResponse.from(savedPlatformFee);
    }
    
    @Override
    @Transactional
    public PlatformFeeResponse updatePlatformFee(UUID id, PlatformFeeUpdateRequest request)
    {
        
        // Find existing platform fee
        PlatformFee platformFee = platformFeeRepository.findById(id)
            .orElseThrow(() -> new PlatformFeeNotFoundException(id));
        
        // Validate that at least one field is provided for update
        if (!request.hasUpdates())
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // Validate update data
        validatePlatformFeeUpdate(id, request);
        
        // Update order if provided
        if (request.hasOrderId())
        {
            Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException(request.orderId()));
            platformFee.setOrder(order);
        }
        
        // Update fee type if provided (with duplicate check)
        if (request.hasFeeType())
        {
            UUID checkOrderId = request.hasOrderId() ? request.orderId() : platformFee.getOrder().getId();
            if (isDuplicateFeeType(checkOrderId, request.feeType()) && 
                !request.feeType().equals(platformFee.getFeeType()))
            {
                throw new DuplicateFeeTypeException(checkOrderId, request.feeType());
            }
            platformFee.setFeeType(request.trimmedFeeType());
        }
        
        // Update amount if provided
        if (request.hasAmount())
        {
            platformFee.setAmount(request.amount());
        }
        
        PlatformFee savedPlatformFee = platformFeeRepository.save(platformFee);
        return PlatformFeeResponse.from(savedPlatformFee);
    }
    
    @Override
    public PlatformFeeResponse findById(UUID id)
    {
        PlatformFee platformFee = platformFeeRepository.findById(id)
            .orElseThrow(() -> new PlatformFeeNotFoundException(id));
        return PlatformFeeResponse.from(platformFee);
    }
    
    @Override
    public List<PlatformFeeResponse> findAll()
    {
        return platformFeeRepository.findAll().stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deletePlatformFee(UUID id)
    {
        if (!platformFeeRepository.existsById(id))
        {
            throw new PlatformFeeNotFoundException(id);
        }
        platformFeeRepository.deleteById(id);
    }
    
    // * Order-Based Operations
    
    @Override
    public List<PlatformFeeResponse> findByOrderId(UUID orderId)
    {
        return platformFeeRepository.findByOrderId(orderId).stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public BigDecimal getTotalFeesByOrder(UUID orderId)
    {
        BigDecimal total = platformFeeRepository.getTotalFeesByOrderId(orderId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional
    public void removeAllFeesFromOrder(UUID orderId)
    {
        List<PlatformFee> fees = platformFeeRepository.findByOrderId(orderId);
        if (!fees.isEmpty())
        {
            platformFeeRepository.deleteAll(fees);
        }
    }
    
    @Override
    public boolean hasOrderFeeOfType(UUID orderId, String feeType)
    {
        return !platformFeeRepository.findByOrderIdAndFeeType(orderId, feeType.trim()).isEmpty();
    }
    
    // * Fee Type Operations
    
    @Override
    public List<PlatformFeeResponse> findByFeeType(String feeType)
    {
        return platformFeeRepository.findByFeeType(feeType.trim()).stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public BigDecimal getTotalFeesByType(String feeType)
    {
        BigDecimal total = platformFeeRepository.getTotalFeesByType(feeType.trim());
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    public List<PlatformFeeResponse> findByOrderAndFeeType(UUID orderId, String feeType)
    {
        return platformFeeRepository.findByOrderIdAndFeeType(orderId, feeType.trim()).stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Platform-Based Operations
    
    @Override
    public List<PlatformFeeResponse> findByPlatformId(UUID platformId)
    {
        return platformFeeRepository.findByPlatformId(platformId).stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public BigDecimal getTotalFeesByPlatform(UUID platformId)
    {
        List<PlatformFee> fees = platformFeeRepository.findByPlatformId(platformId);
        return fees.stream()
            .map(PlatformFee::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public List<PlatformFeeResponse> findByPlatformName(String platformName)
    {
        return platformFeeRepository.findByPlatformName(platformName.trim()).stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Amount-Based Operations
    
    @Override
    public List<PlatformFeeResponse> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount)
    {
        return platformFeeRepository.findByAmountBetween(minAmount, maxAmount).stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PlatformFeeResponse> findByAmountGreaterThanEqual(BigDecimal minAmount)
    {
        return platformFeeRepository.findByAmountGreaterThanEqual(minAmount).stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PlatformFeeResponse> findByAmountLessThanEqual(BigDecimal maxAmount)
    {
        return platformFeeRepository.findByAmountLessThanEqual(maxAmount).stream()
            .map(PlatformFeeResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Validation Operations
    
    @Override
    public void validatePlatformFeeData(PlatformFeeCreateRequest request)
    {
        if (request == null)
        {
            throw new IllegalArgumentException("Platform fee request cannot be null");
        }
        
        if (!request.isValid())
        {
            throw new IllegalArgumentException("Platform fee request contains invalid data");
        }
        
        if (!isValidFeeType(request.feeType()))
        {
            throw new IllegalArgumentException("Invalid fee type: " + request.feeType() + 
                ". Valid types are: " + String.join(", ", VALID_FEE_TYPES));
        }
        
        if (!isValidFeeAmount(request.amount()))
        {
            throw new InvalidFeeAmountException(request.amount());
        }
    }
    
    @Override
    public void validatePlatformFeeUpdate(UUID id, PlatformFeeUpdateRequest request)
    {
        if (request == null)
        {
            throw new IllegalArgumentException("Platform fee update request cannot be null");
        }
        
        if (request.hasFeeType() && !isValidFeeType(request.feeType()))
        {
            throw new IllegalArgumentException("Invalid fee type: " + request.feeType() + 
                ". Valid types are: " + String.join(", ", VALID_FEE_TYPES));
        }
        
        if (request.hasAmount() && !isValidFeeAmount(request.amount()))
        {
            throw new InvalidFeeAmountException(request.amount());
        }
    }
    
    @Override
    public boolean isValidFeeAmount(BigDecimal amount)
    {
        return amount != null && 
               amount.compareTo(MIN_FEE_AMOUNT) >= 0 && 
               amount.compareTo(MAX_FEE_AMOUNT) <= 0;
    }
    
    @Override
    public boolean isValidFeeType(String feeType)
    {
        return feeType != null && 
               !feeType.trim().isEmpty() && 
               VALID_FEE_TYPES.contains(feeType.toLowerCase().trim());
    }
    
    @Override
    public boolean isDuplicateFeeType(UUID orderId, String feeType)
    {
        return hasOrderFeeOfType(orderId, feeType);
    }
}