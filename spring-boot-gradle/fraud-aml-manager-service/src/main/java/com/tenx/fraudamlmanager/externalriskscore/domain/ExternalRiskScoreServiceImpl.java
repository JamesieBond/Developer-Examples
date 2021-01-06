package com.tenx.fraudamlmanager.externalriskscore.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalRiskScoreServiceImpl implements ExternalRiskScoreService {

    private final ExternalRiskScoreProducer externalRiskScoreProducer;

    private final ExternalRiskScoreEntityService externalRiskScoreEntityService;

    public void generateAndStoreRiskScoreEvent(ExternalRiskScore externalRiskScore) throws ExternalRiskScoreException {
        externalRiskScoreEntityService.saveExternalRiskScore(externalRiskScore);
        externalRiskScoreProducer.publishRiskScoreEvent(externalRiskScore);
    }

}
