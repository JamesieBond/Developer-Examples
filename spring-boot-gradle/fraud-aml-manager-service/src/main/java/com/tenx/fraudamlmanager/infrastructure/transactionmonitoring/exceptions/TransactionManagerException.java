package com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions;

import lombok.Getter;

@Getter
public class TransactionManagerException extends Exception {
    private ErrorDetails errorDetails;

    public TransactionManagerException(int errCode, String message) {
        super(message);
        errorDetails = new ErrorDetails(errCode, message);
    }

    public TransactionManagerException(int errCode, String message, Throwable e) {
        super(message, e);
        errorDetails = new ErrorDetails(errCode, message);
    }

}