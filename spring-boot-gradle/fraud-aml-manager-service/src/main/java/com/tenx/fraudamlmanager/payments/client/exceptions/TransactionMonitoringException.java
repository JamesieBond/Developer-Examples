package com.tenx.fraudamlmanager.payments.client.exceptions;

import lombok.Getter;

@Getter
public class TransactionMonitoringException extends Exception {
    private ErrorDetails errorDetails;

    public TransactionMonitoringException(int errCode, String message) {
        super(message);
        errorDetails = new ErrorDetails(errCode, message);
    }

    public TransactionMonitoringException(int errCode, String message, Throwable e) {
        super(message, e);
        errorDetails = new ErrorDetails(errCode, message);
    }

}