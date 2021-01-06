package com.tenx.fraudamlmanager.application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

  private static final String CASE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  private static final String CASE_DATE_FORMAT_KAFKA = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  private DateUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static LocalDate createLocalDateFromInt(int intDate) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    return LocalDate.parse(String.valueOf(intDate), dateFormatter);
  }

  public static OffsetDateTime createOffsetDateTimeFromIntEpochSecond(int intDate) {
    Instant dateOfBirthInstant = Instant.ofEpochSecond(intDate);
    return dateOfBirthInstant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
  }

  public static LocalDate createLocalDateFromIntEpochSecond(int intDate) {
    Instant dateOfBirthInstant = Instant.ofEpochSecond(intDate);
    return dateOfBirthInstant.atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static String formatDate(Date date) {
    DateFormat dateFormat = new SimpleDateFormat(CASE_DATE_FORMAT);
    return dateFormat.format(date);
  }

  public static Date getDateFromKafkaString(String stringDate) throws ParseException {
    DateFormat df = new SimpleDateFormat(CASE_DATE_FORMAT_KAFKA);
    return df.parse(stringDate);
  }

  public static LocalDateTime getLocalDateTimeFromInstant(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }
}
