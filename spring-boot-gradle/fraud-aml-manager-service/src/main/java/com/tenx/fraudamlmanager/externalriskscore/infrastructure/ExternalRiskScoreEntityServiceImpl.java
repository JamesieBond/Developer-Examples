package com.tenx.fraudamlmanager.externalriskscore.infrastructure;

import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreEntityService;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalRiskScoreEntityServiceImpl implements ExternalRiskScoreEntityService {

    private final ExternalRiskScoreRepository externalRiskScoreRepository;

    public void saveExternalRiskScore(ExternalRiskScore externalRiskScore) throws ExternalRiskScoreException {
        try {
            log.info("External Risk Score entity saving on DB for ID: {}", externalRiskScore.getPartyKey());
            ExternalRiskScoreEntity externalRiskScoreEntity = ExternalRiskScoreEntityMapper.MAPPER
                .toRiskScoreEntity(externalRiskScore);
            externalRiskScoreRepository.save(externalRiskScoreEntity);
        } catch (HibernateException ex) {
            throw new ExternalRiskScoreException("Failed to save ERS Entity for ID: " + externalRiskScore.getPartyKey(),
                ex);
        }
    }
}
