package dev.fincke.hopper.order.buyer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buyers")
public class BuyerController
{
    // * Dependencies
    // repository to access buyers
    private final BuyerRepository repo;

    // * Constructor
    public BuyerController(BuyerRepository repo)
    {
        this.repo = repo;
    }

    // * Routes
    // GET /api/buyers - list all buyers
    @GetMapping
    public List<BuyerDto> list()
    {
        return repo.findAll().stream()
                .map(b -> new BuyerDto(
                        b.getId().toString(),
                        b.getEmail(),
                        b.getName()))
                .collect(Collectors.toList());
    }

    // GET /api/buyers/{id} - get buyer by ID
    @GetMapping("/{id}")
    public ResponseEntity<BuyerDto> getById(@PathVariable String id)
    {
        try {
            UUID buyerId = UUID.fromString(id);
            Optional<Buyer> buyer = repo.findById(buyerId);
            
            if (buyer.isPresent()) {
                Buyer b = buyer.get();
                BuyerDto dto = new BuyerDto(
                        b.getId().toString(),
                        b.getEmail(),
                        b.getName());
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/buyers/email/{email} - find buyer by email
    @GetMapping("/email/{email}")
    public ResponseEntity<BuyerDto> getByEmail(@PathVariable String email)
    {
        Buyer buyer = repo.findByEmail(email.toLowerCase());
        if (buyer != null) {
            BuyerDto dto = new BuyerDto(
                    buyer.getId().toString(),
                    buyer.getEmail(),
                    buyer.getName());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/buyers/search/{name} - search buyers by name
    @GetMapping("/search/{name}")
    public List<BuyerDto> searchByName(@PathVariable String name)
    {
        return repo.findByNameContainingIgnoreCase(name).stream()
                .map(b -> new BuyerDto(
                        b.getId().toString(),
                        b.getEmail(),
                        b.getName()))
                .collect(Collectors.toList());
    }

    // * DTO
    // Represents a buyer in API responses
    static class BuyerDto
    {
        // buyer ID
        private final String id;
        // email address
        private final String email;
        // display name
        private final String name;

        // * Constructor
        public BuyerDto(String id, String email, String name)
        {
            this.id = id;
            this.email = email;
            this.name = name;
        }

        // * Getters (for serialization)
        // buyer ID
        public String getId()
        {
            return id;
        }

        // email
        public String getEmail()
        {
            return email;
        }

        // name
        public String getName()
        {
            return name;
        }
    }
}