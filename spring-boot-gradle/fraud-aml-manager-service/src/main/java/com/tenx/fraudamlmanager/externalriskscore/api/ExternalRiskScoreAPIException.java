package com.tenx.fraudamlmanager.externalriskscore.api;

import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreException;
import com.tenx.fraudamlmanager.payments.client.exceptions.ErrorDetails;
import lombok.Getter;

@Getter
public class ExternalRiskScoreAPIException extends ExternalRiskScoreException {

    private ErrorDetails errorDetails;

    public ExternalRiskScoreAPIException(int errCode, String message) {
        super(message);
        errorDetails = new ErrorDetails(errCode, message);
    }

    public ExternalRiskScoreAPIException(int errCode, String message, Throwable e) {
        super(message, e);
        errorDetails = new ErrorDetails(errCode, message);
    }

}