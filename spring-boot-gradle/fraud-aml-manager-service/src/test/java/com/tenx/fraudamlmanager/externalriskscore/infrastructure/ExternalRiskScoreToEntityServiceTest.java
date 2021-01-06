package com.tenx.fraudamlmanager.externalriskscore.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreEntityService;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ExternalRiskScoreToEntityServiceTest {

    @MockBean
    ExternalRiskScoreRepository externalRiskScoreRepository;
    @Captor
    ArgumentCaptor<ExternalRiskScoreEntity> externalRiskScoreEntityArgumentCaptor;
    private ExternalRiskScoreEntityService externalRiskScoreEntityService;

    @BeforeEach
    public void setUp() {
        externalRiskScoreEntityService = new ExternalRiskScoreEntityServiceImpl(externalRiskScoreRepository);
    }

    @Test
    public void checkERSRepositorySave() throws ExternalRiskScoreException {

        ExternalRiskScore externalRiskScore = new ExternalRiskScore("b5c19440-5ad3-465d-8d32-e341fe2cc534", "80",
            "James");

        externalRiskScoreEntityService.saveExternalRiskScore(externalRiskScore);

        verify(externalRiskScoreRepository, times(1)).save(externalRiskScoreEntityArgumentCaptor.capture());
        ExternalRiskScoreEntity externalRiskScoreEntity = externalRiskScoreEntityArgumentCaptor.getValue();
        assertEquals(externalRiskScore.getPartyKey(), externalRiskScoreEntity.getPartyKey());
        assertEquals(externalRiskScore.getProvider(), externalRiskScoreEntity.getProvider());
        assertEquals(externalRiskScore.getRiskScore(), externalRiskScoreEntity.getRiskScore());
    }

}
