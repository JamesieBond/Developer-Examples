package com.tenx.fraudamlmanager.onboarding.business.api;

import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyDetails;
import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyEventService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.business.BusinessEventV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BusinessPartyEventListener {

  private final BusinessPartyEventService businessPartyEventService;

  private final BusinessPartyEventMapperService businessPartyEventMapperService;

  @KafkaListener(id = "BusinessPartyEventListener", containerFactory = "deadLetterQueueKafkaListener",
      topics = "${spring.kafka.consumer.party-event-business-v2-topic}", idIsGroup = false)
  public void handleBusinessPartyEvent(
      ConsumerRecord<String, BusinessEventV2> partyEventCR,
      Acknowledgment acknowledgment,
      @Header("EventType") String eventType) throws TransactionMonitoringException {
    log.info("BusinessPartyDetails event received.");
    BusinessPartyDetails businessPartyDetails = businessPartyEventMapperService
        .mapBusinessPartyDetails(partyEventCR.value(), eventType);
    businessPartyEventService.processBusinessPartyEvent(businessPartyDetails);
    acknowledgment.acknowledge();
  }
}
