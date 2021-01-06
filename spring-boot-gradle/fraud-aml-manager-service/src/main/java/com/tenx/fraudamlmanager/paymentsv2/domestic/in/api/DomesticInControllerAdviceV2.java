package com.tenx.fraudamlmanager.paymentsv2.domestic.in.api;

import com.tenx.fraudamlmanager.paymentsv2.api.ErrorDetails;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringExceptionV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class DomesticInControllerAdviceV2 {

  @ExceptionHandler(value = {DomesticInTransactionMonitoringExceptionV2.class})
  protected ResponseEntity<ErrorDetails> handleDomesticInTransactionMonitoringServiceV2(
          DomesticInTransactionMonitoringExceptionV2 ex) {
    return createErrorDetails(ex);
  }

  private ResponseEntity<ErrorDetails> createErrorDetails(
          DomesticInTransactionMonitoringExceptionV2 exceptionV2) {

    if (DomesticInTransactionMonitoringExceptionV2.Error.MISSING_REQUIRED_FIELDS.equals(exceptionV2.getError())) {
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
