package com.tenx.logging.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class TemporalUtils {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

  public LocalDateTime now() {
    return LocalDateTime.now(ZoneOffset.UTC);
  }

  public String toDateTimeString(final LocalDateTime localDateTime) {
    return DATE_TIME_FORMATTER.format(localDateTime.atOffset(ZoneOffset.UTC));
  }

  public long durationMillis(final LocalDateTime startDateTime) {
    return Duration.between(startDateTime, now()).toMillis();
  }

  public String toStartDateTimeString(long elapsedTime) {
    return DATE_TIME_FORMATTER.format(now().minus(elapsedTime, ChronoUnit.MILLIS).atOffset(ZoneOffset.UTC));
  }

  public static String toDateTimeString(long startTime) {
    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneOffset.UTC);
    return DATE_TIME_FORMATTER.format(dateTime.atOffset(ZoneOffset.UTC));
  }
}
