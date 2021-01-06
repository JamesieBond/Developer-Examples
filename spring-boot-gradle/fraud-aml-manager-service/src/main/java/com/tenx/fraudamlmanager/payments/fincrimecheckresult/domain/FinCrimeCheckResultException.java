package com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.ErrorDetails;
import lombok.Getter;

public class FinCrimeCheckResultException extends Exception {
    @Getter
    private ErrorDetails errorDetails;

    public FinCrimeCheckResultException(int errCode, String message) {
        super(message);
        errorDetails = new ErrorDetails(errCode, message);
    }

    public FinCrimeCheckResultException(int errCode, String message, Throwable e) {
        super(message, e);
        errorDetails = new ErrorDetails(errCode, message);
    }
}
