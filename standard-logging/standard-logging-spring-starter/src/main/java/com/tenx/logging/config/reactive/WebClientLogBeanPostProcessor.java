package com.tenx.logging.config.reactive;

import com.tenx.logging.interceptor.WebClientInterceptor;
import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.util.TemporalUtils;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientLogBeanPostProcessor implements BeanPostProcessor {

    private final OutboundLogger outboundLogger;
    private final TemporalUtils temporalUtils;

    public WebClientLogBeanPostProcessor(OutboundLogger outboundLogger, TemporalUtils temporalUtils) {
        this.temporalUtils = temporalUtils;
        this.outboundLogger = outboundLogger;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof WebClient) {
            final WebClient webClient = (WebClient) bean;
            return webClient.mutate()
                    .filters(addLogExchangeFilterFunctionIfNotPresent()).build();
        } else if (bean instanceof WebClient.Builder) {
            final WebClient.Builder webClientBuilder = (WebClient.Builder) bean;
            return webClientBuilder.filters(addLogExchangeFilterFunctionIfNotPresent());
        }
        return bean;
    }

    private Consumer<List<ExchangeFilterFunction>> addLogExchangeFilterFunctionIfNotPresent() {
        return functions -> {
            if (functions.stream()
                    .noneMatch(function -> function instanceof WebClientInterceptor)) {
                functions.add(new WebClientInterceptor(temporalUtils, outboundLogger));
            }
        };
    }
}