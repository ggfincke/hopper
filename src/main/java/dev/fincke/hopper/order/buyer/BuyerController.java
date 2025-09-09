package dev.fincke.hopper.order.buyer;

import dev.fincke.hopper.order.buyer.dto.BuyerCreateRequest;
import dev.fincke.hopper.order.buyer.dto.BuyerResponse;
import dev.fincke.hopper.order.buyer.dto.BuyerUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// REST controller handling buyer API endpoints
@RestController
@RequestMapping("/api/buyers")
public class BuyerController
{
    
    // * Dependencies
    
    // Spring will inject service dependency
    private final BuyerService buyerService;
    
    // * Constructor
    
    // Constructor injection for BuyerService
    public BuyerController(BuyerService buyerService)
    {
        this.buyerService = buyerService;
    }
    
    // * Core CRUD Endpoints
    
    // POST /api/buyers - create new buyer
    @PostMapping
    public ResponseEntity<BuyerResponse> createBuyer(@Valid @RequestBody BuyerCreateRequest request)
    {
        BuyerResponse response = buyerService.createBuyer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // PUT /api/buyers/{id} - update existing buyer
    @PutMapping("/{id}")
    public BuyerResponse updateBuyer(@PathVariable UUID id, @Valid @RequestBody BuyerUpdateRequest request)
    {
        return buyerService.updateBuyer(id, request);
    }
    
    // GET /api/buyers/{id} - get buyer by ID
    @GetMapping("/{id}")
    public BuyerResponse getById(@PathVariable UUID id)
    {
        return buyerService.findById(id);
    }
    
    // GET /api/buyers - list all buyers
    @GetMapping
    public List<BuyerResponse> getAllBuyers()
    {
        return buyerService.findAll();
    }
    
    // DELETE /api/buyers/{id} - delete buyer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuyer(@PathVariable UUID id)
    {
        buyerService.deleteBuyer(id);
        return ResponseEntity.noContent().build();
    }
    
    // * Query Endpoints
    
    // GET /api/buyers/email/{email} - find buyer by email
    @GetMapping("/email/{email}")
    public BuyerResponse getByEmail(@PathVariable String email)
    {
        return buyerService.findByEmail(email);
    }
    
    // GET /api/buyers/search/{name} - search buyers by name
    @GetMapping("/search/{name}")
    public List<BuyerResponse> searchByName(@PathVariable String name)
    {
        return buyerService.searchByName(name);
    }
    
    // * Utility Endpoints
    
    // GET /api/buyers/exists/email/{email} - check if email exists
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email)
    {
        boolean exists = buyerService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
    
    // POST /api/buyers/{id}/validate - validate buyer data
    @PostMapping("/{id}/validate")
    public ResponseEntity<Void> validateBuyer(@PathVariable UUID id)
    {
        buyerService.validateBuyer(id);
        return ResponseEntity.ok().build();
    }
}