package com.tenx.fraudamlmanager.externalriskscore.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.externalriskscore.infrastructure.ExternalRiskScoreProducerImpl;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author James Spencer
 */
@ExtendWith(SpringExtension.class)
public class ExternalRiskScoreServiceTest {

    private ExternalRiskScoreService externalRiskScoreService;

    @MockBean
    ExternalRiskScoreProducerImpl externalRiskScoreProducer;

    @MockBean
    ExternalRiskScoreEntityService externalRiskScoreEntityService;

    @Captor
    ArgumentCaptor<ExternalRiskScore> externalRiskScoreArgumentCaptor;

    @BeforeEach
    public void beforeEach() {
        this.externalRiskScoreService = new ExternalRiskScoreServiceImpl(externalRiskScoreProducer,
            externalRiskScoreEntityService);
    }

    /**
     * @throws Exception Generic exception
     */
    @Test
    public void checkExternalRiskScoreService() throws TransactionMonitoringException, ExternalRiskScoreException {

        ExternalRiskScore externalRiskScore = new ExternalRiskScore("b5c19440-5ad3-465d-8d32-e341fe2cc534", "80",
            "James");

        externalRiskScoreService.generateAndStoreRiskScoreEvent(externalRiskScore);

        Mockito.verify(externalRiskScoreProducer, times(1))
            .publishRiskScoreEvent(externalRiskScoreArgumentCaptor.capture());

        ExternalRiskScore ersCapturedFirst = externalRiskScoreArgumentCaptor.getValue();
        assertEquals(externalRiskScore.getPartyKey(), ersCapturedFirst.getPartyKey());
        assertEquals(externalRiskScore.getProvider(), ersCapturedFirst.getProvider());
        assertEquals(externalRiskScore.getRiskScore(), ersCapturedFirst.getRiskScore());

        Mockito.verify(externalRiskScoreEntityService, times(1))
            .saveExternalRiskScore(externalRiskScoreArgumentCaptor.capture());

        ExternalRiskScore ersCapturedSecond = externalRiskScoreArgumentCaptor.getValue();
        assertEquals(externalRiskScore.getPartyKey(), ersCapturedSecond.getPartyKey());
        assertEquals(externalRiskScore.getProvider(), ersCapturedSecond.getProvider());
        assertEquals(externalRiskScore.getRiskScore(), ersCapturedSecond.getRiskScore());
    }

}
