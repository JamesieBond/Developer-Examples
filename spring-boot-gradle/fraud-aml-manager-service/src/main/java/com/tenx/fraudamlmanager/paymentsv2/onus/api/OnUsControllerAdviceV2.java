package com.tenx.fraudamlmanager.paymentsv2.onus.api;

import com.tenx.fraudamlmanager.paymentsv2.api.ErrorDetails;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringExceptionV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class OnUsControllerAdviceV2 {

  @ExceptionHandler(value = {OnUsTransactionMonitoringExceptionV2.class})
  protected ResponseEntity<ErrorDetails> handleOnUsTransactionMonitoringServiceV2(
    OnUsTransactionMonitoringExceptionV2 ex) {
    return createErrorDetails(ex);
  }

  private ResponseEntity<ErrorDetails> createErrorDetails(
    OnUsTransactionMonitoringExceptionV2 exceptionV2) {

    if (OnUsTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS.equals(exceptionV2.getError())) {
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
