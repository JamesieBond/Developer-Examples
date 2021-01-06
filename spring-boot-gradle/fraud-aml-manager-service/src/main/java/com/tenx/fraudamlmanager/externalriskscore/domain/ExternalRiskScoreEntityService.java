package com.tenx.fraudamlmanager.externalriskscore.domain;

public interface ExternalRiskScoreEntityService {

    void saveExternalRiskScore(ExternalRiskScore externalRiskScore) throws ExternalRiskScoreException;
}
