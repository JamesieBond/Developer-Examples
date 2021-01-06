package com.tenx.fraudamlmanager.infrastructure;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "feign.retry")
public class RetryProperties {
    private Map<String, RetryConfig> config = new HashMap<>();

    public Map<String, RetryConfig> getConfig() {
        return config;
    }

    @Validated
    @Data
    public static class RetryConfig {
        @Positive
        private long period;

        @Positive
        private long maxPeriod;

        @Positive
        private int maxAttempts;

    }
}
