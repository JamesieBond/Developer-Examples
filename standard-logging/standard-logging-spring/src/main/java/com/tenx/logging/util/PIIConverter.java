package com.tenx.logging.util;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import java.util.function.Predicate;
import java.util.regex.Pattern;


public class PIIConverter extends MessageConverter {

  private static final String PII_DEFAULT_PATTERN_REGEXP = "(\\b(accountNumber|panNumber|ccNumber|iban|bban|firstName|lastName|middleName|dateOfBirth|passport|postcode|email|emailAddress|mobileNumber|mobile|phoneNumber|firstLine|secondLine|thirdLine|city|cityOrTown|county|password|addressLine.*|payeeId|identificationType|identification|placeOfBirth|title|telephone|nationality|taxIdentificationNumber|gender|addressType|countryOfResidence|birthDate|devices|preferredName|givenName|telephoneNumber|state|postCode|country)\\b)|(^((?!spring|java).*?(name))|^((?!spring|java).*?(address)))";
  private static final String PII_EXTENSION_PATTERN_PROPERTY = "extensionPIIPattern";

  private static final String PII_MESSAGE_WARNING = "****** AUTO-MASKED - POTENTIAL PII DATA ******";
  private static final Predicate<String> PII_DEFAULT_PATTERN = Pattern
      .compile(PII_DEFAULT_PATTERN_REGEXP, Pattern.CASE_INSENSITIVE).asPredicate();

  private Predicate<String> piiExtensionPattern = t -> false;

  @Override
  public String convert(ILoggingEvent event) {
    return
        mask(super.convert(event));

  }

  protected String mask(String message) {
    return PII_DEFAULT_PATTERN.test(message) || piiExtensionPattern.test(message) ? PII_MESSAGE_WARNING : message;
  }

  @Override
  public void setContext(Context context) {
    super.setContext(context);
    initExtensionPattern();
  }


  private void initExtensionPattern() {
    String extensionPattern = getContext() == null ? null : getContext().getProperty(PII_EXTENSION_PATTERN_PROPERTY);
    if (extensionPattern != null) {
      piiExtensionPattern = Pattern.compile(extensionPattern, Pattern.CASE_INSENSITIVE).asPredicate();
    }
  }


  protected static String getPiiMessageWarning() {
    return PII_MESSAGE_WARNING;
  }
}

