package com.tenx.fraudamlmanager.externalriskscore.api;

import com.tenx.fraudamlmanager.payments.client.exceptions.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExternalRiskScoreControllerAdvice {

    @ExceptionHandler(value = {ExternalRiskScoreAPIException.class})
    protected ResponseEntity<ErrorDetails> handleERSException(ExternalRiskScoreAPIException ex) {
        log.error("Class: " + ex.getClass() + " - Message: " + ex.getLocalizedMessage() + " - cause: " + ex.getCause());
        return new ResponseEntity<>(ex.getErrorDetails(), HttpStatus.valueOf(ex.getErrorDetails().getHttpStatusCode()));
    }

}
