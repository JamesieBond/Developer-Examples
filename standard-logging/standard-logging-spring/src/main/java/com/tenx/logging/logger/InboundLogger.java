package com.tenx.logging.logger;

import com.tenx.logging.model.InboundLogMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static net.logstash.logback.marker.Markers.appendFields;

@Component
@ConditionalOnProperty(value="logging.level.com.tenx.logging.logger.InboundLogger", havingValue = "INFO")
public class InboundLogger {

  private static final String LOG_MESSAGE = "INBOUND_EVENT";
  private final Logger log = LoggerFactory.getLogger(getClass());

  public void logResponse(InboundLogMarkers markers) {
    log.info(appendFields(markers), LOG_MESSAGE);
  }
}
