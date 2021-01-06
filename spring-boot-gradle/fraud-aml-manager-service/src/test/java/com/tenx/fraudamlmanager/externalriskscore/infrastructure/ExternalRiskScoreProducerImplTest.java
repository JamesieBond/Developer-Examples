package com.tenx.fraudamlmanager.externalriskscore.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraud.ExternalRiskScoreEvent;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ExternalRiskScoreProducerImplTest {

    @Captor
    ArgumentCaptor<ExternalRiskScoreEvent> externalRiskScoreEventArgumentCaptor;

    @MockBean
    private KafkaTemplate<String, ExternalRiskScoreEvent> kafkaProducerTemplate;
    private ExternalRiskScoreProducerImpl externalRiskScoreProducerImpl;

    @BeforeEach
    public void setUp() {
        externalRiskScoreProducerImpl = new ExternalRiskScoreProducerImpl(kafkaProducerTemplate);
    }

    @Test
    void checkERSProducer() throws ExternalRiskScoreException {

        ExternalRiskScore externalRiskScore = new ExternalRiskScore("b5c19440-5ad3-465d-8d32-e341fe2cc534", "80",
            "James");

        externalRiskScoreProducerImpl.publishRiskScoreEvent(externalRiskScore);

        verify(kafkaProducerTemplate, times(1)).send(isNull(), externalRiskScoreEventArgumentCaptor.capture());
        ExternalRiskScoreEvent externalRiskScoreEvent = externalRiskScoreEventArgumentCaptor.getValue();
        assertEquals(externalRiskScore.getPartyKey(), externalRiskScoreEvent.getPartyKey());
        assertEquals(externalRiskScore.getProvider(), externalRiskScoreEvent.getProvider());
        assertEquals(externalRiskScore.getRiskScore(), externalRiskScoreEvent.getRiskScore());
    }

}
