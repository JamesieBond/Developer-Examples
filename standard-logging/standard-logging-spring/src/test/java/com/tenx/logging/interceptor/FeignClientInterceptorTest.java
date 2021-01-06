package com.tenx.logging.interceptor;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.model.OutboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import feign.Request;
import feign.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FeignClientInterceptorTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private OutboundLogger outboundLogger;
    @Mock
    private TemporalUtils temporalUtils;

    @Mock
    private Request request;
    @Mock
    private Request.Options options;
    @Mock
    private Response clientResponse;
    @InjectMocks
    private FeignClientInterceptor classUnderTest;

    @BeforeEach
    public void beforeEach() {
        reset(request);
        when(temporalUtils.now()).thenReturn(NOW);
    }

    @Test
    public void filterCallsLogger() throws IOException {
        FeignClientInterceptor spyInterceptor = Mockito.spy(classUnderTest);
        Mockito.doReturn(clientResponse).when(spyInterceptor).delegateExecute(request, options);
        when(clientResponse.status()).thenReturn(200);
        when(request.httpMethod()).thenReturn(Request.HttpMethod.GET);
        when(request.url()).thenReturn("http://test.local");

        Response response = spyInterceptor.execute(request,options);

        assertThat(response).isEqualTo(clientResponse);
        verify(outboundLogger, times(1)).logResponse(any(OutboundLogMarkers.class));
    }

    @Test
    public void exceptionsAreNotSwallowed() throws IOException {
        when(request.httpMethod()).thenReturn(Request.HttpMethod.GET);
        when(request.url()).thenReturn("http://test.local");

        final String errorMessage = "ErrorOccurred";
        Exception exceptionToThrow = new IOException(errorMessage);

        FeignClientInterceptor spyInterceptor = Mockito.spy(classUnderTest);
        Mockito.doThrow(exceptionToThrow).when(spyInterceptor).delegateExecute(request, options);

        assertThatExceptionOfType(IOException.class)
                .isThrownBy(
                        () ->
                                spyInterceptor.execute(request, options))
                .withMessage(errorMessage);

        verify(outboundLogger, times(1)).logResponse(any(OutboundLogMarkers.class));
    }
}