package com.tenx.fraudamlmanager.authentication.stepup.api;

import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpDetails;
import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.StepUp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StepUpEventListener {

    private final StepUpService stepUpService;


    @KafkaListener(id = "StepUpEventListener", containerFactory = "deadLetterQueueKafkaListener",
        topics = "${spring.kafka.consumer.identity-step-up-v1-topic}", idIsGroup = false)
    public void handleStepUpEvent(
            ConsumerRecord<String, StepUp> stepUpConsumerRecord,
            Acknowledgment acknowledgment) throws TransactionMonitoringException {
        log.info("StepUp event received");
        StepUpDetails stepUpDetails =
            StepUpEventMapper.MAPPER.mapToStepUpDetails(stepUpConsumerRecord.value());
        log.info("StepUp event id {}", stepUpDetails.getPartyKey());
        stepUpService.processStepUpEvent(stepUpDetails);
        acknowledgment.acknowledge();
    }
}
