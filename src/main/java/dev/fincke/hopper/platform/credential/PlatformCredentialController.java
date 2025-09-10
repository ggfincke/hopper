package dev.fincke.hopper.platform.credential;

import dev.fincke.hopper.platform.credential.dto.PlatformCredentialCreateRequest;
import dev.fincke.hopper.platform.credential.dto.PlatformCredentialResponse;
import dev.fincke.hopper.platform.credential.dto.PlatformCredentialUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// REST controller handling platform credential API endpoints
@RestController
@RequestMapping("/api/platform-credentials")
public class PlatformCredentialController
{
    // * Dependencies
    
    // Spring will inject service dependency
    private final PlatformCredentialService credentialService;

    // * Constructor
    
    public PlatformCredentialController(PlatformCredentialService credentialService)
    {
        this.credentialService = credentialService;
    }
    
    // * Core CRUD Endpoints

    // GET /api/platform-credentials - get all credentials with optional filtering
    @GetMapping
    public List<PlatformCredentialResponse> list(@RequestParam(required = false) UUID platformId, 
                                                @RequestParam(required = false) Boolean isActive)
    {
        return credentialService.findAll(platformId, isActive);
    }

    // GET /api/platform-credentials/{id} - get credential by ID
    @GetMapping("/{id}")
    public PlatformCredentialResponse getById(@PathVariable UUID id)
    {
        return credentialService.findById(id);
    }

    // POST /api/platform-credentials - create new credential
    @PostMapping
    public ResponseEntity<PlatformCredentialResponse> create(@Valid @RequestBody PlatformCredentialCreateRequest request)
    {
        PlatformCredentialResponse response = credentialService.createCredential(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/platform-credentials/{id} - update existing credential
    @PutMapping("/{id}")
    public PlatformCredentialResponse update(@PathVariable UUID id, @Valid @RequestBody PlatformCredentialUpdateRequest request)
    {
        return credentialService.updateCredential(id, request);
    }

    // DELETE /api/platform-credentials/{id} - delete credential
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id)
    {
        credentialService.deleteCredential(id);
        return ResponseEntity.noContent().build();
    }
    
    // * Query Endpoints
    
    // GET /api/platform-credentials?platformName={name} - find credentials by platform name
    @GetMapping(params = "platformName")
    public List<PlatformCredentialResponse> getByPlatformName(@RequestParam String platformName,
                                                            @RequestParam(required = false) Boolean isActive)
    {
        if (isActive != null) 
        {
            return credentialService.findByPlatformNameAndIsActive(platformName, isActive);
        }
        return credentialService.findByPlatformName(platformName);
    }
    
    // GET /api/platform-credentials/by-key?platformId={id}&credentialKey={key} - find credential by platform and key
    @GetMapping("/by-key")
    public PlatformCredentialResponse getByPlatformAndKey(@RequestParam UUID platformId,
                                                        @RequestParam String credentialKey)
    {
        return credentialService.findByPlatformIdAndCredentialKey(platformId, credentialKey);
    }
}