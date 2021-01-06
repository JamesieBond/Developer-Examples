package com.tenx.fraudamlmanager.externalriskscore.infrastructure;

import com.tenx.fraud.ExternalRiskScoreEvent;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreException;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalRiskScoreProducerImpl implements ExternalRiskScoreProducer {

    @Value("${spring.kafka.producer.external-risk-score-v1-topic}")
    private String externalRiskScoreTopic;

    private final KafkaTemplate<String, ExternalRiskScoreEvent> kafkaProducerTemplate;

    public void publishRiskScoreEvent(ExternalRiskScore externalRiskScore) throws ExternalRiskScoreException {
        try {
            log.info("Produce External Risk Score event for ID: {}", externalRiskScore.getPartyKey());
            ExternalRiskScoreEvent externalRiskScoreEvent = ExternalRiskScoreEventMapper.MAPPER
                .toRiskScoreEvent(externalRiskScore);
            this.kafkaProducerTemplate.send(externalRiskScoreTopic, externalRiskScoreEvent);
        } catch (KafkaException ex) {
            throw new ExternalRiskScoreException(
                "Failed to produce ERS Event for ID: " + externalRiskScore.getPartyKey(), ex);
        }
    }

}
