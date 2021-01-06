package com.tenx.fraudamlmanager;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import com.tenx.fraudamlmanager.payments.client.exceptions.ErrorDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class FraudAMLControllerAdvice {

  @ExceptionHandler(value = {TransactionMonitoringException.class})
  protected ResponseEntity<ErrorDetails> handleCustomExceptions(TransactionMonitoringException ex) {
    logErrorMessage(ex);
    return new ResponseEntity<>(
        ex.getErrorDetails(), HttpStatus.valueOf(ex.getErrorDetails().getHttpStatusCode()));
  }

  @ExceptionHandler(value = {TransactionManagerException.class})
  protected ResponseEntity<
          com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.ErrorDetails>
      handleTranactionManagerExceptions(TransactionManagerException ex) {
    logErrorMessage(ex);
    return new ResponseEntity<>(
        ex.getErrorDetails(), HttpStatus.valueOf(ex.getErrorDetails().getHttpStatusCode()));
  }

  @ExceptionHandler(value = {FinCrimeCheckResultException.class})
  protected ResponseEntity<
      com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.ErrorDetails>
  handleFinCrimeCheckResultExceptions(FinCrimeCheckResultException ex) {
    logErrorMessage(ex);
    return new ResponseEntity<>(
        ex.getErrorDetails(), HttpStatus.valueOf(ex.getErrorDetails().getHttpStatusCode()));
  }


  private void logErrorMessage(Exception ex) {
    log.error(
        "Class: "
            + ex.getClass()
            + " - Message: "
            + ex.getLocalizedMessage()
            + " - cause: "
            + ex.getCause());
  }
}
