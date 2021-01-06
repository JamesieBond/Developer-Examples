package com.tenx.logging.filter;

import static org.springframework.util.CollectionUtils.isEmpty;

import com.tenx.logging.util.Properties;
import com.tenx.logging.logger.InboundLogger;
import com.tenx.logging.model.InboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import java.time.LocalDateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(1)
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnBean(InboundLogger.class)
public class ReactiveInboundFilter implements WebFilter {

    private final TemporalUtils temporalUtils;
    private final InboundLogger inboundLogger;

    public ReactiveInboundFilter(TemporalUtils temporalUtils, InboundLogger inboundLogger) {
        this.temporalUtils = temporalUtils;
        this.inboundLogger = inboundLogger;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (shouldLogRequest(exchange.getRequest().getURI().toString())) {
            final LocalDateTime startTime = temporalUtils.now();
            return chain.filter(exchange).doFinally(aVoid -> {
                inboundLogger.logResponse(mapToMarkers(startTime, exchange.getRequest(), exchange.getResponse()));
            });
        } else {
            return chain.filter(exchange);
        }
    }

    private boolean shouldLogRequest(String requestURI) {
        return (!isEmpty(Properties.getAllowedPatterns()) && Properties.getAllowedPatterns().stream().anyMatch(p -> p.matcher(requestURI).find()));
    }

    private InboundLogMarkers mapToMarkers(LocalDateTime startTime, ServerHttpRequest request, ServerHttpResponse response) {
        return InboundLogMarkers.builder()
                .start_time(temporalUtils.toDateTimeString(startTime))
                .response_time(temporalUtils.durationMillis(startTime))
                .http_request_method(request.getMethod().name())
                .http_request_host(request.getURI().getHost())
                .http_request_path(request.getURI().getPath())
                .http_request_header_content_type(request.getHeaders().getContentType().toString())
                .http_response_code(response == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : response.getRawStatusCode())
                .build();
    }
}
