package dev.fincke.hopper.order.buyer.exception;

import java.util.UUID;

// Domain exception for buyer lookup failures (carries search criteria for debugging)
public class BuyerNotFoundException extends RuntimeException
{
    
    // Search criteria that failed (for debugging context)
    private final UUID buyerId;
    private final String email;
    
    // Constructor for ID-based lookup failure
    public BuyerNotFoundException(UUID buyerId)
    {
        super("Buyer with ID " + buyerId + " not found");
        this.buyerId = buyerId;
        this.email = null;
    }
    
    // Constructor for email-based lookup failure
    public BuyerNotFoundException(String email)
    {
        super("Buyer with email '" + email + "' not found");
        this.buyerId = null;
        this.email = email;
    }
    
    // buyerId (null if lookup was by email)
    public UUID getBuyerId()
    {
        return buyerId;
    }
    
    // email (null if lookup was by ID)
    public String getEmail()
    {
        return email;
    }
}