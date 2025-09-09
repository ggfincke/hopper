package dev.fincke.hopper.order.buyer;

import dev.fincke.hopper.order.buyer.dto.BuyerCreateRequest;
import dev.fincke.hopper.order.buyer.dto.BuyerResponse;
import dev.fincke.hopper.order.buyer.dto.BuyerUpdateRequest;

import java.util.List;
import java.util.UUID;

// Service interface defining buyer business operations
public interface BuyerService
{
    
    // * Core CRUD Operations
    
    // create new buyer from request data
    BuyerResponse createBuyer(BuyerCreateRequest request);
    
    // update existing buyer by ID
    BuyerResponse updateBuyer(UUID id, BuyerUpdateRequest request);
    
    // find buyer by ID (throws exception if not found)
    BuyerResponse findById(UUID id);
    
    // retrieve all buyers
    List<BuyerResponse> findAll();
    
    // delete buyer by ID
    void deleteBuyer(UUID id);
    
    // * Email-Based Operations
    
    // find buyer by email address
    BuyerResponse findByEmail(String email);
    
    // check if buyer exists with given email
    boolean existsByEmail(String email);
    
    // * Search Operations
    
    // search buyers by name (case-insensitive partial match)
    List<BuyerResponse> searchByName(String name);
    
    // * Validation Operations
    
    // validate buyer data and business rules
    void validateBuyer(UUID id);
    
    // check if email format is valid
    boolean isValidEmail(String email);
}