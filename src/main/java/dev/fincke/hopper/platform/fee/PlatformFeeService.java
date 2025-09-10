package dev.fincke.hopper.platform.fee;

import dev.fincke.hopper.platform.fee.dto.PlatformFeeCreateRequest;
import dev.fincke.hopper.platform.fee.dto.PlatformFeeResponse;
import dev.fincke.hopper.platform.fee.dto.PlatformFeeUpdateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Service interface defining platform fee business operations
public interface PlatformFeeService
{
    
    // * Core CRUD Operations
    
    // create new platform fee from request data with validation
    PlatformFeeResponse createPlatformFee(PlatformFeeCreateRequest request);
    
    // update existing platform fee by ID
    PlatformFeeResponse updatePlatformFee(UUID id, PlatformFeeUpdateRequest request);
    
    // find platform fee by ID (throws exception if not found)
    PlatformFeeResponse findById(UUID id);
    
    // retrieve all platform fees
    List<PlatformFeeResponse> findAll();
    
    // delete platform fee by ID
    void deletePlatformFee(UUID id);
    
    // * Order-Based Operations
    
    // find all fees for specific order
    List<PlatformFeeResponse> findByOrderId(UUID orderId);
    
    // calculate total fee amount for all fees in an order
    BigDecimal getTotalFeesByOrder(UUID orderId);
    
    // remove all fees from an order (useful for order cancellation)
    void removeAllFeesFromOrder(UUID orderId);
    
    // check if order has any fees of specified type
    boolean hasOrderFeeOfType(UUID orderId, String feeType);
    
    // * Fee Type Operations
    
    // find all fees of specific type
    List<PlatformFeeResponse> findByFeeType(String feeType);
    
    // calculate total amount for all fees of specific type
    BigDecimal getTotalFeesByType(String feeType);
    
    // find fees by order and fee type combination
    List<PlatformFeeResponse> findByOrderAndFeeType(UUID orderId, String feeType);
    
    // * Platform-Based Operations
    
    // find all fees for specific platform
    List<PlatformFeeResponse> findByPlatformId(UUID platformId);
    
    // calculate total fee amount for all fees on a platform
    BigDecimal getTotalFeesByPlatform(UUID platformId);
    
    // find fees by platform name
    List<PlatformFeeResponse> findByPlatformName(String platformName);
    
    // * Amount-Based Operations
    
    // find fees within amount range
    List<PlatformFeeResponse> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // find fees greater than or equal to minimum amount
    List<PlatformFeeResponse> findByAmountGreaterThanEqual(BigDecimal minAmount);
    
    // find fees less than or equal to maximum amount
    List<PlatformFeeResponse> findByAmountLessThanEqual(BigDecimal maxAmount);
    
    // * Validation Operations
    
    // validate platform fee data meets business requirements
    void validatePlatformFeeData(PlatformFeeCreateRequest request);
    
    // validate platform fee update data
    void validatePlatformFeeUpdate(UUID id, PlatformFeeUpdateRequest request);
    
    // validate fee amount is within acceptable range
    boolean isValidFeeAmount(BigDecimal amount);
    
    // validate fee type is supported
    boolean isValidFeeType(String feeType);
    
    // check if duplicate fee type exists for order (business rule enforcement)
    boolean isDuplicateFeeType(UUID orderId, String feeType);
}