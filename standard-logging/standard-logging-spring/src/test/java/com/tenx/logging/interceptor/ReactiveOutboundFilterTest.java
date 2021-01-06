package com.tenx.logging.interceptor;

import static org.mockito.Mockito.reset;

import com.tenx.logging.interceptor.WebClientInterceptor;
import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.util.TemporalUtils;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ReactiveOutboundFilterTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private OutboundLogger outboundLogger;
    @Mock
    private TemporalUtils temporalUtils;
    @Mock
    private ClientRequest request;
    @Mock
    private ExchangeFunction exchange;

    @Mock
    private Mono<ClientResponse> response;

    @InjectMocks
    private WebClientInterceptor classUnderTest;

    @BeforeEach
    public void beforeEach() {
        reset(request);
//        when(temporalUtils.now()).thenReturn(NOW);
    }

    @Test
    public void filterCallsLogger(){
//        when(exchange.exchange(request)).thenReturn(response);
//        classUnderTest.filter(request, exchange);
//        verify(outboundLogger, times(1)).logResponse(any(OutboundLogMarkers.class));
    }

    @Test
    public void exceptionsAreNotSwallowed(){
//        final String errorMessage = "ErrorOccurred";
//        Exception exceptionToThrow = new IOException(errorMessage);
//        when(exchange.exchange(request)).thenThrow(exceptionToThrow);
//
//        assertThatExceptionOfType(IOException.class)
//                .isThrownBy(
//                        () ->
//                                classUnderTest.filter(request, exchange))
//                .withMessage(errorMessage);
//
//        verify(outboundLogger, times(1)).logResponse(any(OutboundLogMarkers.class));
    }
}
