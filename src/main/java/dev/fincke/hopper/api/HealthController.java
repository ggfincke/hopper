package dev.fincke.hopper.api;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class Health {
    private final String status;
    private final String service;
    private final String version;

    public Health() {
        this("ok", "hopper", null);
    }

    public Health(String status, String service, String version) {
        this.status = status;
        this.service = service;
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public String getService() {
        return service;
    }

    public String getVersion() {
        return version;
    }
}

@RestController
public class HealthController {

    private final BuildProperties build;

    public HealthController(BuildProperties build) {
        this.build = build;
    }

    @GetMapping("/healthz")
    public Health health() {
        return new Health("ok", "hopper", build.getVersion());
    }
}