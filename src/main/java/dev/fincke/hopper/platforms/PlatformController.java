package dev.fincke.hopper.platforms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

class PlatformDto 
{
    private final String id;
    private final String name;
    private final String platformType;

    public PlatformDto(String id, String name, String platformType) 
    {
        this.id = id;
        this.name = name;
        this.platformType = platformType;
    }

    public String getId() 
    {
        return id;
    }

    public String getName() 
    {
        return name;
    }

    public String getPlatformType() 
    {
        return platformType;
    }
}

@RestController
@RequestMapping("/api/platforms")
public class PlatformController 
{
    
    private final PlatformRepository repo;

    public PlatformController(PlatformRepository repo) 
    {
        this.repo = repo;
    }

    @GetMapping
    public List<PlatformDto> list() 
    {
        return repo.findAll().stream()
                .map(platform -> new PlatformDto(
                    platform.getId().toString(),
                    platform.getName(),
                    platform.getPlatformType()))
                .collect(Collectors.toList());
    }
}