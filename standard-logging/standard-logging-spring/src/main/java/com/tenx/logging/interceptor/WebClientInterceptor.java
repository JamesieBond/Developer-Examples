package com.tenx.logging.interceptor;

import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.model.OutboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class WebClientInterceptor implements ExchangeFilterFunction {

    private final TemporalUtils temporalUtils;
    private final OutboundLogger outboundLogger;

    public WebClientInterceptor(TemporalUtils temporalUtils, OutboundLogger outboundLogger) {
        this.temporalUtils = temporalUtils;
        this.outboundLogger = outboundLogger;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        final LocalDateTime startTime = temporalUtils.now();
        return next.exchange(request)
                .doOnSuccess(response -> {
                    outboundLogger.logResponse(mapToMarkers(startTime, request, response));
                })
                .doOnError(throwable -> {
                    outboundLogger.logResponse(mapToMarkers(startTime, request, null));
                });
    }

    private OutboundLogMarkers mapToMarkers(LocalDateTime startTime, ClientRequest request, ClientResponse response){
        return OutboundLogMarkers.builder()
                .start_time(temporalUtils.toDateTimeString(startTime))
                .response_time(temporalUtils.durationMillis(startTime))
                .http_request_method(request.method().name())
                .http_request_host(request.url().getHost())
                .http_request_path(request.url().getPath())
                .http_response_code(response == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : response.rawStatusCode())
                .build();
    }
}
