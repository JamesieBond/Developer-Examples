package com.tenx.fraudamlmanager.onboarding.payee.api;

import com.tenx.fraudamlmanager.onboarding.payee.domain.PayeesEventService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.payeemanager.event.payee.PayeesCreate;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import com.tenx.payeemanager.event.payee.PayeesUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(id = "payeeEvent", containerFactory = "deadLetterQueueKafkaListener",
    topics = "${spring.kafka.consumer.payee-event-v1-topic}", idIsGroup = false)
public class PayeeEventListener {

  private final PayeesEventService payeesEventService;

  @KafkaHandler
  public void handlePayeeEvent(PayeesCreate payeesCreate, Acknowledgment acknowledgment)
      throws TransactionMonitoringException {
    log.info("PayeeCreate event received.");
    payeesEventService.processPayeeCreateEvent(payeesCreate);
    acknowledgment.acknowledge();
  }

  @KafkaHandler
  public void handlePayeesDeleteEvent(PayeesDelete payeesDelete, Acknowledgment acknowledgment)
      throws TransactionMonitoringException {
    log.info("PayeeDelete event received.");
    payeesEventService.processPayeeDeleteEvent(payeesDelete);
    acknowledgment.acknowledge();
  }

  @KafkaHandler
  public void handlePayeesUpdateEvent(PayeesUpdate payeesUpdate, Acknowledgment acknowledgment)
      throws TransactionMonitoringException {
    log.info("PayeeUpdate event received.");
    payeesEventService.processPayeeUpdateEvent(payeesUpdate);
    acknowledgment.acknowledge();
  }
}
