package dev.fincke.hopper.platform.fee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PlatformFeeRepository extends JpaRepository<PlatformFee, UUID> 
{
    
    List<PlatformFee> findByOrderId(UUID orderId);
    
    List<PlatformFee> findByFeeType(String feeType);
    
    List<PlatformFee> findByOrderIdAndFeeType(UUID orderId, String feeType);
    
    List<PlatformFee> findByAmountGreaterThanEqual(BigDecimal minAmount);
    
    List<PlatformFee> findByAmountLessThanEqual(BigDecimal maxAmount);
    
    List<PlatformFee> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    @Query("SELECT pf FROM PlatformFee pf WHERE pf.order.platform.id = :platformId")
    List<PlatformFee> findByPlatformId(@Param("platformId") UUID platformId);
    
    @Query("SELECT pf FROM PlatformFee pf WHERE pf.order.platform.name = :platformName")
    List<PlatformFee> findByPlatformName(@Param("platformName") String platformName);
    
    @Query("SELECT SUM(pf.amount) FROM PlatformFee pf WHERE pf.order.id = :orderId")
    BigDecimal getTotalFeesByOrderId(@Param("orderId") UUID orderId);
    
    @Query("SELECT SUM(pf.amount) FROM PlatformFee pf WHERE pf.feeType = :feeType")
    BigDecimal getTotalFeesByType(@Param("feeType") String feeType);
}