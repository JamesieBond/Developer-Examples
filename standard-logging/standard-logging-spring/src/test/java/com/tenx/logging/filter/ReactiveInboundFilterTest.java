package com.tenx.logging.filter;

import com.tenx.logging.util.Properties;
import com.tenx.logging.logger.InboundLogger;
import com.tenx.logging.model.InboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReactiveInboundFilterTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private InboundLogger inboundLogger;
    @Mock
    private TemporalUtils temporalUtils;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private WebFilterChain chain;
    @InjectMocks
    private ReactiveInboundFilter classUnderTest;

    private static MockedStatic<Properties> mockedProperties;

    @BeforeAll
    public static void setUp() {
        mockedProperties = mockStatic(Properties.class);

    }

    @AfterAll
    public static void tearDown() {
      mockedProperties.close();
    }

    @BeforeEach
    public void beforeEach() {
        reset(request);
    }

    @Test
    public void filterCallsLoggerWithIncludedUri() throws URISyntaxException {
        mockedProperties.when(Properties::getInboundWebFilterIncluding).thenReturn(singletonList("v\\d+\\/*"));
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(new URI("http://test.local/v1"));
//        when(temporalUtils.now()).thenReturn(NOW);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        classUnderTest.filter(exchange, chain);

        verify(chain).filter(exchange);
//        verify(inboundLogger,times(1)).logResponse(any(InboundLogMarkers.class));
    }

    @Test
    public void filterCallsLoggerWithoutIncludedUri() throws URISyntaxException {
        mockedProperties.when(Properties::getInboundWebFilterIncluding).thenReturn(asList("", "bar*"));
        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(new URI("http://test.local/foo"));
//        when(temporalUtils.now()).thenReturn(NOW);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        classUnderTest.filter(exchange, chain);

        verify(chain).filter(exchange);
//        verify(inboundLogger,times(1)).logResponse(any(InboundLogMarkers.class));
    }

    @Test
    public void filterCallsLoggerWithIncludedUri_exceptionsAreNotSwallowed() throws URISyntaxException {
//        final String filterErrorMessage = "FilterErrorOccurred";
//        Exception exceptionToThrow = new RuntimeException(filterErrorMessage);
//        doThrow(exceptionToThrow).when(chain).filter(exchange);
//
//        mockedProperties.when(Properties::getInboundWebFilterIncluding).thenReturn(singletonList("v\\d+\\/*"));
//
//        when(request.getURI()).thenReturn(new URI("http://test.local/v1"));
//        when(temporalUtils.now()).thenReturn(NOW);
//
//        assertThatExceptionOfType(RuntimeException.class)
//                .isThrownBy(() ->
//                        classUnderTest.filter(exchange, chain))
//                .withMessage(filterErrorMessage);

//        verify(inboundLogger, times(1)).logResponse(any(InboundLogMarkers.class));
    }
}
