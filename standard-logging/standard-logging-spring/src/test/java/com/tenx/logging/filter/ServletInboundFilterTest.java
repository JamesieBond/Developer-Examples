package com.tenx.logging.filter;

import com.tenx.logging.util.Properties;
import com.tenx.logging.logger.InboundLogger;
import com.tenx.logging.model.InboundLogMarkers;
import com.tenx.logging.util.TemporalUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServletInboundFilterTest {

  private static final LocalDateTime NOW = LocalDateTime.now();

  @Mock
  private InboundLogger inboundLogger;
  @Mock
  private TemporalUtils temporalUtils;
  @Mock
  private HttpServletRequest servletRequest;
  @Mock
  private HttpServletResponse servletResponse;
  @Mock
  private FilterChain filterChain;

  private static MockedStatic<Properties> mockedProperties;

  @BeforeAll
  public static void setUp() {
    mockedProperties = mockStatic(Properties.class);
  }

  @AfterAll
  public static void tearDown() {
    mockedProperties.close();
  }

  @Test
  public void filterCallsLoggerWithIncludedUri() throws ServletException, IOException {
    mockedProperties.when(Properties::getAllowedPatterns).thenReturn(singletonList(Pattern.compile("v\\d+\\/*")));
    ServletInboundFilter classUnderTest = new ServletInboundFilter(temporalUtils, inboundLogger);

    when(servletRequest.getRequestURI()).thenReturn("v1");
    when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://test.local/v1"));
    when(temporalUtils.now()).thenReturn(NOW);

    classUnderTest.doFilterInternal(servletRequest, servletResponse, filterChain);

    verify(filterChain).doFilter(servletRequest, servletResponse);
    verify(inboundLogger,times(1)).logResponse(any(InboundLogMarkers.class));
  }

  @Test
  public void filterCallsLoggerWithoutIncludedUri() throws ServletException, IOException {
    mockedProperties.when(Properties::getAllowedPatterns).thenReturn(asList(Pattern.compile("v1"), Pattern.compile("bar*")));
    ServletInboundFilter classUnderTest = new ServletInboundFilter(temporalUtils, inboundLogger);

    when(servletRequest.getRequestURI()).thenReturn("foo");

    classUnderTest.doFilterInternal(servletRequest, servletResponse, filterChain);

    verify(filterChain).doFilter(servletRequest, servletResponse);
    verifyNoInteractions(inboundLogger);
  }

  @Test
  public void filterCallsLoggerWithIncludedUri_exceptionsAreNotSwallowed() throws ServletException, IOException {

    final String filterErrorMessage = "FilterErrorOccurred";
    Exception exceptionToThrow = new RuntimeException(filterErrorMessage);
    doThrow(exceptionToThrow).when(filterChain).doFilter(servletRequest, servletResponse);

    mockedProperties.when(Properties::getAllowedPatterns).thenReturn(singletonList(Pattern.compile("v\\d+\\/*")));
    ServletInboundFilter classUnderTest = new ServletInboundFilter(temporalUtils, inboundLogger);

    when(servletRequest.getRequestURI()).thenReturn("v1");
    when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://test.local/v1"));
    when(temporalUtils.now()).thenReturn(NOW);

    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() ->
                classUnderTest.doFilterInternal(servletRequest, servletResponse, filterChain))
        .withMessage(filterErrorMessage);

    verify(inboundLogger, times(1)).logResponse(any(InboundLogMarkers.class));
  }
}
