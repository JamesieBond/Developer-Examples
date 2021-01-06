package com.tenx.logging.filter;

import static org.springframework.util.CollectionUtils.isEmpty;

import com.tenx.logging.util.Properties;
import com.tenx.logging.logger.InboundLogger;
import com.tenx.logging.model.InboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Order(1)
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(InboundLogger.class)
public class ServletInboundFilter extends OncePerRequestFilter {

  private final TemporalUtils temporalUtils;
  private final InboundLogger inboundLogger;


  public ServletInboundFilter(TemporalUtils temporalUtils, InboundLogger inboundLogger) {
    this.temporalUtils = temporalUtils;
    this.inboundLogger = inboundLogger;
  }

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

    if (shouldLogRequest(request.getRequestURI())) {
      final LocalDateTime startTime = temporalUtils.now();
      try {
        chain.doFilter(request, response);
      } finally {
        inboundLogger.logResponse(mapToMarkers(startTime, request, response));
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  private boolean shouldLogRequest(String requestURI) {
    return (!isEmpty(Properties.getAllowedPatterns()) && Properties.getAllowedPatterns().stream().anyMatch(p -> p.matcher(requestURI).find()));
  }

  private InboundLogMarkers mapToMarkers(LocalDateTime startTime, HttpServletRequest request, HttpServletResponse response) {
    final UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString()).build();
    return InboundLogMarkers.builder()
            .start_time(temporalUtils.toDateTimeString(startTime))
            .response_time(temporalUtils.durationMillis(startTime))
            .http_request_method(request.getMethod())
            .http_request_host(uriComponents.getHost())
            .http_request_path(uriComponents.getPath())
            .http_request_header_content_type(request.getContentType())
            .http_response_code(response == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : response.getStatus())
            .build();
  }
}