package com.tenx.logging.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TemporalUtilsTest {

  private static final String NOW = "2020-09-23 18:10:40.123";
  private TemporalUtils classUnderTest = Mockito.spy(new TemporalUtils());

  @Test
  public void testToDateTimeString_PassingLocalDateTime() {
    //when
    String result = classUnderTest.toDateTimeString(getLocalDateTimeOfNow());
    //then
    assertThat(result).isEqualTo("2020-09-23T18:10:40.123Z");
  }

  @Test
  public void testToStartDateTimeString() {
    //given
    when(classUnderTest.now()).thenReturn(getLocalDateTimeOfNow());
    //when
    String result = classUnderTest.toStartDateTimeString(10);
    //then
    assertThat(result).isEqualTo("2020-09-23T18:10:40.113Z");
  }

  @Test
  public void testToDateTimeString_PassingLong() {
    long dateTimeLong = getLocalDateTimeOfNow().toInstant(ZoneOffset.UTC).toEpochMilli();
    //when
    String result = classUnderTest.toDateTimeString(dateTimeLong);
    //then
    assertThat(result).isEqualTo("2020-09-23T18:10:40.123Z");
  }

  private LocalDateTime getLocalDateTimeOfNow() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    return LocalDateTime.parse(NOW, formatter);
  }

}