package com.tenx.logging.interceptor;

import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.model.OutboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import feign.Client;
import feign.Request;
import feign.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class FeignClientInterceptor implements Client {

    private final OutboundLogger outboundLogger;
    private final TemporalUtils temporalUtils;
    private final BeanFactory beanFactory;
    private final Client delegate;

    public FeignClientInterceptor(BeanFactory beanFactory, Client delegate, OutboundLogger outboundLogger, TemporalUtils temporalUtils) {
        this.beanFactory = beanFactory;
        this.delegate = delegate;
        this.outboundLogger = outboundLogger;
        this.temporalUtils = temporalUtils;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        Response response = null;
        final LocalDateTime startTime = temporalUtils.now();
        try {
            response = delegateExecute(request, options);
        } finally {
            outboundLogger.logResponse(mapToMarkers(startTime, request, response));
        }
        return response;
    }

    Response delegateExecute(Request request, Request.Options options) throws IOException {
        return delegate.execute(request, options);
    }

    private OutboundLogMarkers mapToMarkers(LocalDateTime startTime, Request request, Response response){
        final UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(request.url()).build();
        return OutboundLogMarkers.builder()
                .start_time(temporalUtils.toDateTimeString(startTime))
                .response_time(temporalUtils.durationMillis(startTime))
                .http_request_method(request.httpMethod().name())
                .http_request_host(uriComponents.getHost())
                .http_request_path(uriComponents.getPath())
                .http_response_code(response == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : response.status())
                .build();
    }
}
