package com.tenx.fraudamlmanager.externalriskscore.domain;

public interface ExternalRiskScoreProducer {

    void publishRiskScoreEvent(ExternalRiskScore externalRiskScore) throws ExternalRiskScoreException;
}
