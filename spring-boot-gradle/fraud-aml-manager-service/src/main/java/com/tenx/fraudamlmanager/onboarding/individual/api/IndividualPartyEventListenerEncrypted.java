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
@ConditionalOnProperty(value = "spring.kafka.security.enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class IndividualPartyEventListenerEncrypted {

  private final IndividualPartyEventService individualPartyEventService;

  @KafkaListener(id = "IndividualPartyEventEncrypted", containerFactory = "encryptedContainerFactory",
      topics = "${spring.kafka.consumer.party-event-v3-encrypted-topic}", idIsGroup = false)
  public void handleIndividualPartyEvent(ConsumerRecord<String, CustomerEventV3> partyEventCR,
      Acknowledgment acknowledgment, @Header("EventType") String eventType) {
    log.info("IndividualPartyDetails event received.");
    try {
      individualPartyEventService.processIndividualPartyEvent(partyEventCR.value(), eventType);
      acknowledgment.acknowledge();
    } catch (TransactionMonitoringException e) {
      log.error("Exception thrown while processing the event {} ", TransactionMonitoringException.class);
    }

  }
//
//
//  public void handleIndividualPartyEvent(
//      ConsumerRecord<String, Object> partyEventCR, Acknowledgment acknowledgment,
//      @Header("EventType") String eventType) {
//    log.info("IndividualPartyDetails event received.");
//    try {
//      ensureValueIsCustomerEventV3(partyEventCR);
//      individualPartyEventService.processIndividualPartyEvent((CustomerEventV3) partyEventCR.value(), eventType);
//      acknowledgment.acknowledge();
//    } catch (TransactionMonitoringException e) {
//      log.error("Exception thrown while processing the event {} ", TransactionMonitoringException.class);
//    }
//
//  }
//
//  private void ensureValueIsCustomerEventV3(ConsumerRecord<String, Object> consumerRecord) {
//    if (!(consumerRecord.value() instanceof CustomerEventV3)) {
//      String type = consumerRecord.value() != null ? consumerRecord.value().getClass().getName() : null;
//      throw new UnexpectedTypeException(
//          "Message value is not correct type [" + type + "]. Key [" + consumerRecord.key() + "]");
//    }
//  }
}
