package com.tenx.fraudamlmanager.externalriskscore.domain;

public class ExternalRiskScoreException extends Exception {

    public ExternalRiskScoreException(String message) {
        super(message);
    }

    public ExternalRiskScoreException(String message, Throwable e) {
        super(message, e);
    }

}