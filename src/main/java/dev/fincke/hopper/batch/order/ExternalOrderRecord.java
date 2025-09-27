package dev.fincke.hopper.batch.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Lightweight snapshot of an external marketplace order used by the batch layer.
// Contains only the fields required for normalization before handing off to the domain.
public record ExternalOrderRecord(
    String platformCode,
    String externalOrderId,
    UUID buyerId,
    BigDecimal totalAmount,
    Instant orderDate
) {}
