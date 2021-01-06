package com.tenx.logging.config.reactive;

import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.util.TemporalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnClass({WebClient.class, OutboundLogger.class})
public class WebClientLogAutoConfig {

    @Autowired(required = false)
    private OutboundLogger outboundLogger;

    @Autowired
    private TemporalUtils temporalUtils;

    @Bean
    public WebClientLogBeanPostProcessor loggingWebClientBeanPostProcessor() {
        return new WebClientLogBeanPostProcessor(outboundLogger, temporalUtils);
    }
}
