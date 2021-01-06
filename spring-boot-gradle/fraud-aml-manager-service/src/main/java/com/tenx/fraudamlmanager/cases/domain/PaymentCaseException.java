package com.tenx.fraudamlmanager.cases.domain;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.ErrorDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class PaymentCaseException extends Exception {
  @Getter private ErrorDetails errorDetails;

  public PaymentCaseException(String s) {
    super(s);
    errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), s);
  }

  public PaymentCaseException(String s, Throwable t) {
    super(s, t);
    errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), s);
  }
}
