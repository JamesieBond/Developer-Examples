package com.tenx.fraudamlmanager.paymentsv2.direct.debit.api;

import com.tenx.fraudamlmanager.paymentsv2.api.ErrorDetails;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitTransactionMonitoringExceptionV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class DirectDebitControllerAdviceV2 {

  @ExceptionHandler(value = {DirectDebitTransactionMonitoringExceptionV2.class})
  protected ResponseEntity<ErrorDetails> handleDirectDebitTransactionMonitoringServiceV2(
      DirectDebitTransactionMonitoringExceptionV2 ex) {
    return createErrorDetails(ex);
  }

  private ResponseEntity<ErrorDetails> createErrorDetails(
      DirectDebitTransactionMonitoringExceptionV2 exceptionV2) {

    if (DirectDebitTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS.equals(exceptionV2.getError())) {
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
