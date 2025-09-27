package dev.fincke.hopper.marketplace.client.model;

// * Enum
// Normalized order lifecycle states shared between Java orchestration and connectors.
public enum OrderStatus
{
    PENDING,
    CONFIRMED,
    FAILED
}
