package com.tenx.fraudamlmanager.paymentsv3.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.ErrorDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Niall O'Connell
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandlerV3 {

    @ExceptionHandler(value = {TransactionMonitoringException.class})
    protected ResponseEntity<ErrorDetails> handleCustomExceptions(TransactionMonitoringException ex) {
        String error =
            "Class: " + ex.getClass() + " - Message: " + ex.getLocalizedMessage() + " - cause: " + ex.getCause();
        log.error(error);
        return new ResponseEntity<>(ex.getErrorDetails(), HttpStatus.valueOf(ex.getErrorDetails().getHttpStatusCode()));
    }

}
