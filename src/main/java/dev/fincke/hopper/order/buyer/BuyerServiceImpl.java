package dev.fincke.hopper.order.buyer;

import dev.fincke.hopper.order.buyer.dto.BuyerCreateRequest;
import dev.fincke.hopper.order.buyer.dto.BuyerResponse;
import dev.fincke.hopper.order.buyer.dto.BuyerUpdateRequest;
import dev.fincke.hopper.order.buyer.exception.BuyerNotFoundException;
import dev.fincke.hopper.order.buyer.exception.DuplicateEmailException;
import dev.fincke.hopper.order.buyer.exception.BuyerDeletionNotAllowedException;
import dev.fincke.hopper.order.order.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Service implementation for buyer business operations with transaction management
// Spring service with read-only transactions by default
@Service
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class BuyerServiceImpl implements BuyerService
{
    
    // * Dependencies
    
    // Repository for buyer data access
    private final BuyerRepository buyerRepository;
    private final OrderRepository orderRepository;
    
    // Email pattern for additional validation beyond @Email annotation (business rule)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // * Constructor
    
    // Constructor injection for repository dependency
    public BuyerServiceImpl(BuyerRepository buyerRepository, OrderRepository orderRepository)
    {
        this.buyerRepository = buyerRepository;
        this.orderRepository = orderRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional
    public BuyerResponse createBuyer(BuyerCreateRequest request)
    {
        
        // Validate email uniqueness if email is provided (business rule)
        if (request.email() != null && buyerRepository.existsByEmail(request.email()))
        {
            throw new DuplicateEmailException(request.email());
        }
        
        // Create and populate entity
        Buyer buyer = new Buyer(request.email(), request.name());
        
        // Save and return response DTO
        Buyer savedBuyer = buyerRepository.save(buyer);
        return BuyerResponse.from(savedBuyer);
    }
    
    @Override
    @Transactional
    public BuyerResponse updateBuyer(UUID id, BuyerUpdateRequest request)
    {
        
        // Find existing buyer
        Buyer buyer = buyerRepository.findById(id)
            .orElseThrow(() -> new BuyerNotFoundException(id));
        
        // Validate that at least one field is provided for update
        if (!request.hasUpdates())
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // Update email if provided and different
        if (request.email() != null && !request.email().equals(buyer.getEmail()))
        {
            // Check for email uniqueness
            if (buyerRepository.existsByEmail(request.email()))
            {
                throw new DuplicateEmailException(request.email());
            }
            buyer.setEmail(request.email());
        }
        
        // Update name if provided
        if (request.name() != null)
        {
            buyer.setName(request.name());
        }
        
        Buyer savedBuyer = buyerRepository.save(buyer);
        return BuyerResponse.from(savedBuyer);
    }
    
    @Override
    public BuyerResponse findById(UUID id)
    {
        Buyer buyer = buyerRepository.findById(id)
            .orElseThrow(() -> new BuyerNotFoundException(id));
        return BuyerResponse.from(buyer);
    }
    
    @Override
    public List<BuyerResponse> findAll()
    {
        return buyerRepository.findAll().stream()
            .map(BuyerResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteBuyer(UUID id)
    {
        
        if (!buyerRepository.existsById(id))
        {
            throw new BuyerNotFoundException(id);
        }

        if (orderRepository.existsByBuyerId(id))
        {
            throw new BuyerDeletionNotAllowedException(id);
        }

        buyerRepository.deleteById(id);
    }
    
    // * Email-Based Operations
    
    @Override
    public BuyerResponse findByEmail(String email)
    {
        
        if (email == null || email.trim().isEmpty())
        {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        String normalizedEmail = email.trim().toLowerCase();
        Buyer buyer = buyerRepository.findByEmail(normalizedEmail);
        
        if (buyer == null)
        {
            throw new BuyerNotFoundException(normalizedEmail);
        }
        
        return BuyerResponse.from(buyer);
    }
    
    @Override
    public boolean existsByEmail(String email)
    {
        
        if (email == null || email.trim().isEmpty())
        {
            return false;
        }
        
        String normalizedEmail = email.trim().toLowerCase();
        return buyerRepository.existsByEmail(normalizedEmail);
    }
    
    // * Search Operations
    
    @Override
    public List<BuyerResponse> searchByName(String name)
    {
        
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Search name cannot be null or empty");
        }
        
        String searchTerm = name.trim();
        return buyerRepository.findByNameContainingIgnoreCase(searchTerm).stream()
            .map(BuyerResponse::from)
            .collect(Collectors.toList());
    }
    
    // * Validation Operations
    
    @Override
    public void validateBuyer(UUID id)
    {
        
        if (!buyerRepository.existsById(id))
        {
            throw new BuyerNotFoundException(id);
        }
        
        // Additional validation logic can be added here
        // such as checking for active orders, payment methods, etc.
    }
    
    @Override
    public boolean isValidEmail(String email)
    {
        
        if (email == null || email.trim().isEmpty())
        {
            return false;
        }
        
        String trimmedEmail = email.trim();
        
        // Basic length check
        if (trimmedEmail.length() > 254)
        {
            return false;
        }
        
        // Pattern matching for email format
        return EMAIL_PATTERN.matcher(trimmedEmail).matches();
    }
}
