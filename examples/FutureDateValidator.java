package com.tenx.universalbanking.interestcalculator.model.validators;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FutureDateValidator implements ConstraintValidator<FutureDate, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {
      ZonedDateTime zonedDateTime = ZonedDateTime.parse(value);
      return !zonedDateTime.toLocalDate().isAfter(LocalDate.now(zonedDateTime.getZone()));
    } catch (DateTimeParseException ex) {
      return false;
    }
  }
}