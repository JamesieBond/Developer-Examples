package com.tenx.fraudamlmanager.onboarding.individual.api;

import com.tenx.fraudamlmanager.onboarding.individual.domain.IndividualPartyEventService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.CustomerEventV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "spring.kafka.security.enabled", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
public class IndividualPartyEventListener {

  private final IndividualPartyEventService individualPartyEventService;

  @KafkaListener(id = "IndividualPartyEvent", containerFactory = "deadLetterQueueKafkaListener",
      topics = "${spring.kafka.consumer.party-event-v3-topic}", idIsGroup = false)
  public void handleIndividualPartyEvent(
      ConsumerRecord<String, CustomerEventV3> partyEventCR, Acknowledgment acknowledgment,
      @Header("EventType") String eventType) {
    log.info("IndividualPartyDetails event received.");
    try {
      individualPartyEventService.processIndividualPartyEvent(partyEventCR.value(), eventType);
      acknowledgment.acknowledge();
    } catch (TransactionMonitoringException e) {
      log.error("Exception thrown while processing the event {} ", TransactionMonitoringException.class);
    }

  }

}
