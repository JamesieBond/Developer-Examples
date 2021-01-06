package com.tenx.logging.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;

import java.io.IOException;
import java.util.UUID;

public class UniqueIdProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

  private final String uniqueInstanceId = UUID.randomUUID().toString();

  @Override
  public void writeTo(JsonGenerator generator, ILoggingEvent iLoggingEvent) throws IOException {
    JsonWritingUtils.writeStringField(generator, "unique_instance_id", uniqueInstanceId);
  }

}
