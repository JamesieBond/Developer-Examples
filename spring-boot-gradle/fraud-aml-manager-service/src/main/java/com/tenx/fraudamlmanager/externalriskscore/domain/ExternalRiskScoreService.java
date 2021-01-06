package com.tenx.fraudamlmanager.externalriskscore.domain;

public interface ExternalRiskScoreService {

    void generateAndStoreRiskScoreEvent(ExternalRiskScore externalRiskScore) throws ExternalRiskScoreException;
}
