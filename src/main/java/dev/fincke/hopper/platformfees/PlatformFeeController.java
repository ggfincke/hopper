package dev.fincke.hopper.platformfees;

import dev.fincke.hopper.orders.Order;
import dev.fincke.hopper.orders.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class PlatformFeeDto 
{
    private final String id;
    private final String orderId;
    private final String feeType;
    private final BigDecimal amount;

    public PlatformFeeDto(String id, String orderId, String feeType, BigDecimal amount) 
    {
        this.id = id;
        this.orderId = orderId;
        this.feeType = feeType;
        this.amount = amount;
    }

    public String getId() 
    {
        return id;
    }

    public String getOrderId() 
    {
        return orderId;
    }

    public String getFeeType() 
    {
        return feeType;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
}

class CreatePlatformFeeRequest 
{
    private String orderId;
    private String feeType;
    private BigDecimal amount;

    public String getOrderId() 
    {
        return orderId;
    }

    public void setOrderId(String orderId) 
    {
        this.orderId = orderId;
    }

    public String getFeeType() 
    {
        return feeType;
    }

    public void setFeeType(String feeType) 
    {
        this.feeType = feeType;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }
}

@RestController
@RequestMapping("/api/platform-fees")
public class PlatformFeeController 
{
    
    private final PlatformFeeRepository repo;
    private final OrderRepository orderRepo;

    public PlatformFeeController(PlatformFeeRepository repo, OrderRepository orderRepo) 
    {
        this.repo = repo;
        this.orderRepo = orderRepo;
    }

    @GetMapping
    public List<PlatformFeeDto> list(@RequestParam(required = false) UUID orderId,
                                    @RequestParam(required = false) String feeType,
                                    @RequestParam(required = false) BigDecimal minAmount,
                                    @RequestParam(required = false) BigDecimal maxAmount) 
    {
        List<PlatformFee> fees;
        
        if (orderId != null && feeType != null) {
            fees = repo.findByOrderIdAndFeeType(orderId, feeType);
        } else if (orderId != null) {
            fees = repo.findByOrderId(orderId);
        } else if (feeType != null) {
            fees = repo.findByFeeType(feeType);
        } else if (minAmount != null && maxAmount != null) {
            fees = repo.findByAmountBetween(minAmount, maxAmount);
        } else if (minAmount != null) {
            fees = repo.findByAmountGreaterThanEqual(minAmount);
        } else if (maxAmount != null) {
            fees = repo.findByAmountLessThanEqual(maxAmount);
        } else {
            fees = repo.findAll();
        }

        return fees.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatformFeeDto> getById(@PathVariable UUID id) 
    {
        return repo.findById(id)
                .map(fee -> ResponseEntity.ok(toDto(fee)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlatformFeeDto> create(@RequestBody CreatePlatformFeeRequest request) 
    {
        try {
            UUID orderId = UUID.fromString(request.getOrderId());
            Order order = orderRepo.findById(orderId).orElse(null);
            
            if (order == null) {
                return ResponseEntity.badRequest().build();
            }

            PlatformFee fee = new PlatformFee(
                order,
                request.getFeeType(),
                request.getAmount()
            );

            PlatformFee saved = repo.save(fee);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatformFeeDto> update(@PathVariable UUID id, @RequestBody CreatePlatformFeeRequest request) 
    {
        return repo.findById(id)
                .map(fee -> {
                    try {
                        UUID orderId = UUID.fromString(request.getOrderId());
                        Order order = orderRepo.findById(orderId).orElse(null);
                        
                        if (order == null) {
                            return ResponseEntity.badRequest().<PlatformFeeDto>build();
                        }

                        fee.setOrder(order);
                        fee.setFeeType(request.getFeeType());
                        fee.setAmount(request.getAmount());

                        PlatformFee saved = repo.save(fee);
                        return ResponseEntity.ok(toDto(saved));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().<PlatformFeeDto>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) 
    {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/totals/by-order/{orderId}")
    public ResponseEntity<BigDecimal> getTotalFeesByOrder(@PathVariable UUID orderId) 
    {
        BigDecimal total = repo.getTotalFeesByOrderId(orderId);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    @GetMapping("/totals/by-type/{feeType}")
    public ResponseEntity<BigDecimal> getTotalFeesByType(@PathVariable String feeType) 
    {
        BigDecimal total = repo.getTotalFeesByType(feeType);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    private PlatformFeeDto toDto(PlatformFee fee) 
    {
        return new PlatformFeeDto(
            fee.getId().toString(),
            fee.getOrder().getId().toString(),
            fee.getFeeType(),
            fee.getAmount()
        );
    }
}