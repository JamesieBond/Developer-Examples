package com.tenx.fraudamlmanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public void health() {
        // TODO: configure kubernentes service to point to the REAL health endpoint /actuator/health
    }

}