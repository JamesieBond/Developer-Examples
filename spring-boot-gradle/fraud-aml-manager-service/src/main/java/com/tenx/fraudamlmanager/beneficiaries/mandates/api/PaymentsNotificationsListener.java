package com.tenx.fraudamlmanager.beneficiaries.mandates.api;

import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.PaymentsNotificationsService;
import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.SetupMandatesDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.payment.configuration.directdebit.event.v1.DirectDebitEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentsNotificationsListener {

  private final PaymentsNotificationsService paymentsNotificationsService;

  @KafkaListener(id = "PaymentsNotificationsListener", containerFactory = "deadLetterQueueKafkaListener",
      topics = "${spring.kafka.consumer.payments-notifications-topic}", idIsGroup = false)
  public void handleDirectDebitEvent(
      ConsumerRecord<String, DirectDebitEvent> paymentsNotificationsEventCR, Acknowledgment acknowledgment)
      throws TransactionMonitoringException {
    DirectDebitEvent event = paymentsNotificationsEventCR.value();
    log.info("DirectDebitEvent received for partyKey {}", event.getPartyKey());
    SetupMandatesDetails setupMandatesDetails = PaymentsNotificationsEventMapper.MAPPER
        .mapToSetupMandatesDetails(event);
    paymentsNotificationsService.processMandatesBeneficiary(setupMandatesDetails);
    acknowledgment.acknowledge();
  }
}
