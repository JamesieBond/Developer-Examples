package com.tenx.logging.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.model.OutboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

@ExtendWith(MockitoExtension.class)
public class WebClientInterceptorTest {

  private static final LocalDateTime NOW = LocalDateTime.now();

  @Mock
  private OutboundLogger outboundLogger;
  @Mock
  private TemporalUtils temporalUtils;
  @InjectMocks
  private RestTemplateInterceptor classUnderTest;

  @Mock
  private HttpRequest httpRequest;
  @Mock
  private ClientHttpRequestExecution clientHttpRequestExecution;
  @Mock
  private ClientHttpResponse clientHttpResponse;

  private byte[] body = new byte[]{};

  @BeforeEach
  public void beforeEach() {
    reset(httpRequest, clientHttpRequestExecution);
    when(temporalUtils.now()).thenReturn(NOW);
  }

  @Test
  public void filterCallsLogger() throws IOException, URISyntaxException {

    when(clientHttpRequestExecution.execute(httpRequest, body)).thenReturn(clientHttpResponse);
    when(clientHttpResponse.getRawStatusCode()).thenReturn(200);
    when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
    when(httpRequest.getURI()).thenReturn(new URI("http://test.local"));

    ClientHttpResponse response = classUnderTest.intercept(httpRequest, body, clientHttpRequestExecution);

    assertThat(response).isEqualTo(clientHttpResponse);
    verify(outboundLogger, times(1)).logResponse(any(OutboundLogMarkers.class));
  }

  @Test
  public void exceptionsAreNotSwallowed() throws IOException, URISyntaxException {
    when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
    when(httpRequest.getURI()).thenReturn(new URI("http://test.local"));

    final String errorMessage = "ErrorOccurred";
    Exception exceptionToThrow = new IOException(errorMessage);
    doThrow(exceptionToThrow).when(clientHttpRequestExecution).execute(httpRequest, body);

    assertThatExceptionOfType(IOException.class)
        .isThrownBy(
            () ->
                classUnderTest.intercept(httpRequest, body, clientHttpRequestExecution))
        .withMessage(errorMessage);

    verify(outboundLogger, times(1)).logResponse(any(OutboundLogMarkers.class));
  }
}
