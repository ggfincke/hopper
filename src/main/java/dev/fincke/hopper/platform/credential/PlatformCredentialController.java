package dev.fincke.hopper.platform.credential;

import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.platform.platform.PlatformRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class PlatformCredentialDto 
{
    private final String id;
    private final String platformId;
    private final String platformName;
    private final String credentialKey;
    private final String credentialValue;
    private final Boolean isActive;

    public PlatformCredentialDto(String id, String platformId, String platformName, String credentialKey, String credentialValue, Boolean isActive) 
    {
        this.id = id;
        this.platformId = platformId;
        this.platformName = platformName;
        this.credentialKey = credentialKey;
        this.credentialValue = credentialValue;
        this.isActive = isActive;
    }

    public String getId() 
    {
        return id;
    }

    public String getPlatformId() 
    {
        return platformId;
    }

    public String getPlatformName() 
    {
        return platformName;
    }

    public String getCredentialKey() 
    {
        return credentialKey;
    }

    public String getCredentialValue() 
    {
        return credentialValue;
    }

    public Boolean getIsActive() 
    {
        return isActive;
    }
}

class CreatePlatformCredentialRequest 
{
    private String platformId;
    private String credentialKey;
    private String credentialValue;
    private Boolean isActive = true;

    public String getPlatformId() 
    {
        return platformId;
    }

    public void setPlatformId(String platformId) 
    {
        this.platformId = platformId;
    }

    public String getCredentialKey() 
    {
        return credentialKey;
    }

    public void setCredentialKey(String credentialKey) 
    {
        this.credentialKey = credentialKey;
    }

    public String getCredentialValue() 
    {
        return credentialValue;
    }

    public void setCredentialValue(String credentialValue) 
    {
        this.credentialValue = credentialValue;
    }

    public Boolean getIsActive() 
    {
        return isActive;
    }

    public void setIsActive(Boolean isActive) 
    {
        this.isActive = isActive;
    }
}

@RestController
@RequestMapping("/api/platform-credentials")
public class PlatformCredentialController 
{
    
    private final PlatformCredentialRepository repo;
    private final PlatformRepository platformRepo;

    public PlatformCredentialController(PlatformCredentialRepository repo, PlatformRepository platformRepo) 
    {
        this.repo = repo;
        this.platformRepo = platformRepo;
    }

    @GetMapping
    public List<PlatformCredentialDto> list(@RequestParam(required = false) UUID platformId, 
                                           @RequestParam(required = false) Boolean isActive) 
    {
        List<PlatformCredential> credentials;
        
        if (platformId != null && isActive != null) {
            credentials = repo.findByPlatformIdAndIsActive(platformId, isActive);
        } else if (platformId != null) {
            credentials = repo.findByPlatformId(platformId);
        } else if (isActive != null) {
            credentials = repo.findByIsActive(isActive);
        } else {
            credentials = repo.findAll();
        }

        return credentials.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatformCredentialDto> getById(@PathVariable UUID id) 
    {
        return repo.findById(id)
                .map(credential -> ResponseEntity.ok(toDto(credential)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlatformCredentialDto> create(@RequestBody CreatePlatformCredentialRequest request) 
    {
        try {
            UUID platformId = UUID.fromString(request.getPlatformId());
            Platform platform = platformRepo.findById(platformId).orElse(null);
            
            if (platform == null) {
                return ResponseEntity.badRequest().build();
            }

            PlatformCredential credential = new PlatformCredential(
                platform,
                request.getCredentialKey(),
                request.getCredentialValue(),
                request.getIsActive()
            );

            PlatformCredential saved = repo.save(credential);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatformCredentialDto> update(@PathVariable UUID id, @RequestBody CreatePlatformCredentialRequest request) 
    {
        return repo.findById(id)
                .map(credential -> {
                    try {
                        UUID platformId = UUID.fromString(request.getPlatformId());
                        Platform platform = platformRepo.findById(platformId).orElse(null);
                        
                        if (platform == null) {
                            return ResponseEntity.badRequest().<PlatformCredentialDto>build();
                        }

                        credential.setPlatform(platform);
                        credential.setCredentialKey(request.getCredentialKey());
                        credential.setCredentialValue(request.getCredentialValue());
                        credential.setIsActive(request.getIsActive());

                        PlatformCredential saved = repo.save(credential);
                        return ResponseEntity.ok(toDto(saved));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().<PlatformCredentialDto>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) 
    {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private PlatformCredentialDto toDto(PlatformCredential credential) 
    {
        return new PlatformCredentialDto(
            credential.getId().toString(),
            credential.getPlatform().getId().toString(),
            credential.getPlatform().getName(),
            credential.getCredentialKey(),
            "***REDACTED***", // never expose credential values in API responses
            credential.getIsActive()
        );
    }
}