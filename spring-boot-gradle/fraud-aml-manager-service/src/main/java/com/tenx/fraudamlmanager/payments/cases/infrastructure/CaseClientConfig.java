package com.tenx.fraudamlmanager.payments.cases.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.RetryProperties;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@EnableConfigurationProperties(RetryProperties.class)
public class CaseClientConfig {

    public static final String CLIENT_NAME = "case-governor";

    @Autowired
    private RetryProperties retryProperties;

    /**
     * @return retryer
     */
    @Bean
    public Retryer retryer() {

        RetryProperties.RetryConfig config = retryProperties.getConfig().get(CLIENT_NAME);
        return new Retryer.Default(config.getPeriod(), config.getMaxPeriod(), config.getMaxAttempts());
    }

}
