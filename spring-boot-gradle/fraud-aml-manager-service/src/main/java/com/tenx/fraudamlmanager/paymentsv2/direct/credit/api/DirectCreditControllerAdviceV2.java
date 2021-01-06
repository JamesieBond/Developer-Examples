package com.tenx.fraudamlmanager.paymentsv2.direct.credit.api;

import com.tenx.fraudamlmanager.paymentsv2.api.ErrorDetails;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditTransactionMonitoringExceptionV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class DirectCreditControllerAdviceV2 {

  @ExceptionHandler(value = {DirectCreditTransactionMonitoringExceptionV2.class})
  protected ResponseEntity<ErrorDetails> handleDirectCreditTransactionMonitoringServiceV2(
      DirectCreditTransactionMonitoringExceptionV2 ex) {
    return createErrorDetails(ex);
  }

  private ResponseEntity<ErrorDetails> createErrorDetails(
      DirectCreditTransactionMonitoringExceptionV2 exceptionV2) {

    if (DirectCreditTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS.equals(exceptionV2.getError())) {
      return new ResponseEntity<ErrorDetails>(
          new ErrorDetails(HttpStatus.BAD_REQUEST.value(), exceptionV2.getMessage()),
          HttpStatus.BAD_REQUEST);

    } else {
      return new ResponseEntity<ErrorDetails>(
          new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionV2.getMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
