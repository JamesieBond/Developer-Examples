package com.tenx.fraudamlmanager.customer.paymentinfo.api;


import com.tenx.fraudamlmanager.customer.paymentinfo.domain.CustomerCreditTransferEventService;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferException;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerCreditTransferEventListener {

  private final CustomerCreditTransferEventService customerCreditTransferEventService;

  @KafkaListener(id = "CustomerCreditTransferInitiationCompletedEventListener", containerFactory = "deadLetterQueueKafkaListener",
      topics = "${spring.kafka.consumer.payments-cct-initiation-completed-topic}", idIsGroup = false)
  public void handleCustomerCreditTransferInitiationCompletedEvent(
      ConsumerRecord<String, CustomerCreditTransferInitiationCompletedEvent> cctiCompletedEvent,
      Acknowledgment acknowledgment) throws CustomerCreditTransferException {

    CustomerCreditTransferInitiationCompletedEvent customerCreditTransferInitiationCompletedEvent = cctiCompletedEvent
        .value();

    String transactionTraceIdentification = GenericCustomerCreditTransfMapper.extractTransactionTraceIdentification(
        customerCreditTransferInitiationCompletedEvent);

    log.info("Received CustomerCreditTransferInitiationCompletedEvent with transaction identifier: {}",
        transactionTraceIdentification);

    customerCreditTransferEventService
        .processCustomerCreditTransferEvent(customerCreditTransferInitiationCompletedEvent,
            transactionTraceIdentification);
    acknowledgment.acknowledge();

  }
}

