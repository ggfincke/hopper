package dev.fincke.hopper.batch.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight representation of an order retrieved from an external marketplace API.
 */
public record ExternalOrderRecord(
    String platformCode,
    String externalOrderId,
    UUID buyerId,
    BigDecimal totalAmount,
    Instant orderDate
) {}
