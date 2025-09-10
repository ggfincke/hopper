package dev.fincke.hopper.platform.fee;

import dev.fincke.hopper.platform.fee.dto.PlatformFeeCreateRequest;
import dev.fincke.hopper.platform.fee.dto.PlatformFeeResponse;
import dev.fincke.hopper.platform.fee.dto.PlatformFeeUpdateRequest;
import dev.fincke.hopper.platform.fee.exception.PlatformFeeNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// REST controller for platform fee API endpoints (delegates business logic to service layer)
@RestController
@RequestMapping("/api/platform-fees")
public class PlatformFeeController
{
    
    // * Dependencies
    
    // Service layer for platform fee business operations
    private final PlatformFeeService platformFeeService;
    
    // * Constructor
    
    // Constructor injection for service dependency
    public PlatformFeeController(PlatformFeeService platformFeeService)
    {
        this.platformFeeService = platformFeeService;
    }
    
    // * API Endpoints
    
    // GET /api/platform-fees - list platform fees with optional filtering
    @GetMapping
    public ResponseEntity<List<PlatformFeeResponse>> list(
        @RequestParam(required = false) UUID orderId,
        @RequestParam(required = false) String feeType,
        @RequestParam(required = false) BigDecimal minAmount,
        @RequestParam(required = false) BigDecimal maxAmount,
        @RequestParam(required = false) UUID platformId,
        @RequestParam(required = false) String platformName)
    {
        
        List<PlatformFeeResponse> fees;
        
        // Apply filters based on request parameters (delegating to service layer)
        if (orderId != null && feeType != null)
        {
            fees = platformFeeService.findByOrderAndFeeType(orderId, feeType);
        }
        else if (orderId != null)
        {
            fees = platformFeeService.findByOrderId(orderId);
        }
        else if (feeType != null)
        {
            fees = platformFeeService.findByFeeType(feeType);
        }
        else if (platformId != null)
        {
            fees = platformFeeService.findByPlatformId(platformId);
        }
        else if (platformName != null)
        {
            fees = platformFeeService.findByPlatformName(platformName);
        }
        else if (minAmount != null && maxAmount != null)
        {
            fees = platformFeeService.findByAmountBetween(minAmount, maxAmount);
        }
        else if (minAmount != null)
        {
            fees = platformFeeService.findByAmountGreaterThanEqual(minAmount);
        }
        else if (maxAmount != null)
        {
            fees = platformFeeService.findByAmountLessThanEqual(maxAmount);
        }
        else
        {
            fees = platformFeeService.findAll();
        }
        
        return ResponseEntity.ok(fees);
    }
    
    // GET /api/platform-fees/{id} - get platform fee by ID
    @GetMapping("/{id}")
    public ResponseEntity<PlatformFeeResponse> getById(@PathVariable UUID id)
    {
        try
        {
            PlatformFeeResponse fee = platformFeeService.findById(id);
            return ResponseEntity.ok(fee);
        }
        catch (PlatformFeeNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /api/platform-fees - create new platform fee
    @PostMapping
    public ResponseEntity<PlatformFeeResponse> create(@Valid @RequestBody PlatformFeeCreateRequest request)
    {
        try
        {
            // Delegate to service layer for business logic and validation
            PlatformFeeResponse createdFee = platformFeeService.createPlatformFee(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFee);
        }
        catch (IllegalArgumentException e)
        {
            // Return bad request for validation errors
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PUT /api/platform-fees/{id} - update platform fee
    @PutMapping("/{id}")
    public ResponseEntity<PlatformFeeResponse> update(
        @PathVariable UUID id, 
        @Valid @RequestBody PlatformFeeUpdateRequest request)
    {
        try
        {
            // Delegate to service layer for business logic and validation
            PlatformFeeResponse updatedFee = platformFeeService.updatePlatformFee(id, request);
            return ResponseEntity.ok(updatedFee);
        }
        catch (PlatformFeeNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DELETE /api/platform-fees/{id} - delete platform fee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id)
    {
        try
        {
            platformFeeService.deletePlatformFee(id);
            return ResponseEntity.noContent().build();
        }
        catch (PlatformFeeNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // * Aggregate Endpoints
    
    // GET /api/platform-fees/totals/by-order/{orderId} - get total fees for order
    @GetMapping("/totals/by-order/{orderId}")
    public ResponseEntity<BigDecimal> getTotalFeesByOrder(@PathVariable UUID orderId)
    {
        BigDecimal total = platformFeeService.getTotalFeesByOrder(orderId);
        return ResponseEntity.ok(total);
    }
    
    // GET /api/platform-fees/totals/by-type/{feeType} - get total fees by type
    @GetMapping("/totals/by-type/{feeType}")
    public ResponseEntity<BigDecimal> getTotalFeesByType(@PathVariable String feeType)
    {
        BigDecimal total = platformFeeService.getTotalFeesByType(feeType);
        return ResponseEntity.ok(total);
    }
    
    // GET /api/platform-fees/totals/by-platform/{platformId} - get total fees for platform
    @GetMapping("/totals/by-platform/{platformId}")
    public ResponseEntity<BigDecimal> getTotalFeesByPlatform(@PathVariable UUID platformId)
    {
        BigDecimal total = platformFeeService.getTotalFeesByPlatform(platformId);
        return ResponseEntity.ok(total);
    }
    
    // DELETE /api/platform-fees/by-order/{orderId} - remove all fees from order
    @DeleteMapping("/by-order/{orderId}")
    public ResponseEntity<Void> removeAllFeesFromOrder(@PathVariable UUID orderId)
    {
        platformFeeService.removeAllFeesFromOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}