package dev.fincke.hopper.batch.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Command object passed from the batch layer into the order import workflow.
// Keeps downstream services isolated from marketplace-specific representations.
public record OrderImportRequest(
    String platformCode,
    String externalOrderId,
    UUID buyerId,
    BigDecimal totalAmount,
    Instant orderDate
) {}
