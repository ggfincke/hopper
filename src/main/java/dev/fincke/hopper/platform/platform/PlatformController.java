package dev.fincke.hopper.platform.platform;

import dev.fincke.hopper.platform.platform.dto.PlatformCreateRequest;
import dev.fincke.hopper.platform.platform.dto.PlatformResponse;
import dev.fincke.hopper.platform.platform.dto.PlatformUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// REST controller handling platform API endpoints
@RestController
@RequestMapping("/api/platforms")
public class PlatformController
{
    // * Dependencies
    
    // Spring will inject service dependency
    private final PlatformService platformService;

    // * Constructor
    
    public PlatformController(PlatformService platformService)
    {
        this.platformService = platformService;
    }

    // * Core CRUD Endpoints
    
    // POST /api/platforms - create new platform
    @PostMapping
    public ResponseEntity<PlatformResponse> createPlatform(@Valid @RequestBody PlatformCreateRequest request)
    {
        PlatformResponse response = platformService.createPlatform(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // PUT /api/platforms/{id} - update existing platform
    @PutMapping("/{id}")
    public PlatformResponse updatePlatform(@PathVariable UUID id, @Valid @RequestBody PlatformUpdateRequest request)
    {
        return platformService.updatePlatform(id, request);
    }
    
    // GET /api/platforms/{id} - get platform by ID
    @GetMapping("/{id}")
    public PlatformResponse getById(@PathVariable UUID id)
    {
        return platformService.findById(id);
    }
    
    // GET /api/platforms - get all platforms
    @GetMapping
    public List<PlatformResponse> getAllPlatforms()
    {
        return platformService.findAll();
    }
    
    // DELETE /api/platforms/{id} - delete platform
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable UUID id)
    {
        platformService.deletePlatform(id);
        return ResponseEntity.noContent().build();
    }
    
    // * Query Endpoints
    
    // GET /api/platforms?platformType={type} - find platforms by type
    @GetMapping(params = "platformType")
    public List<PlatformResponse> getByPlatformType(@RequestParam String platformType)
    {
        return platformService.findByPlatformType(platformType);
    }
    
    // GET /api/platforms?name={name} - find platform by name
    @GetMapping(params = "name")
    public PlatformResponse getByName(@RequestParam String name)
    {
        return platformService.findByName(name);
    }
}