package dev.fincke.hopper.batch.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Command object used to hand off normalized order data to the domain services.
 */
public record OrderImportRequest(
    String platformCode,
    String externalOrderId,
    UUID buyerId,
    BigDecimal totalAmount,
    Instant orderDate
) {}
