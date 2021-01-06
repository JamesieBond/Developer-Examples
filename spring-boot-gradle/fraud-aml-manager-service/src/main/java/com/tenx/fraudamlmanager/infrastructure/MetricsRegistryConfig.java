package com.tenx.fraudamlmanager.infrastructure;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Micrometer registry configuration
 */
@Configuration
@EnableAspectJAutoProxy
public class MetricsRegistryConfig {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("fraud.fraudamlmanager", "Fraud & AML Manager");
    }
}
