package com.tenx.logging.interceptor;

import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.model.OutboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(OutboundLogger.class)
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

  private final OutboundLogger outboundLogger;
  private final TemporalUtils temporalUtils;

  public RestTemplateInterceptor(OutboundLogger outboundLogger, TemporalUtils temporalUtils) {
    this.outboundLogger = outboundLogger;
    this.temporalUtils = temporalUtils;
  }

  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

    ClientHttpResponse response = null;
    final LocalDateTime startTime = temporalUtils.now();
    try {
      response = execution.execute(request, body);
    } finally {
      outboundLogger.logResponse(mapToMarkers(startTime, request, response));
    }
    return response;
  }

  private OutboundLogMarkers mapToMarkers(LocalDateTime startTime, HttpRequest request, ClientHttpResponse response) throws IOException {
    return OutboundLogMarkers.builder()
            .start_time(temporalUtils.toDateTimeString(startTime))
            .response_time(temporalUtils.durationMillis(startTime))
            .http_request_method(request.getMethod().name())
            .http_request_host(request.getURI().getHost())
            .http_request_path(request.getURI().getPath())
            .http_response_code(response == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : response.getRawStatusCode())
            .build();
  }
}

