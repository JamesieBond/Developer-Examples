package com.tenx.fraudamlmanager.paymentsv3.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/** @author Niall O'Connell */
public class DateUtilsV3 {

  private static final String CASE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  private DateUtilsV3() {
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
}
