package com.tenx.fraudamlmanager.beneficiaries.mandates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.beneficiaries.mandates.api.PaymentsNotificationsListener;
import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.BeneficiaryAction;
import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.SetupMandates;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.payment.configuration.directdebit.event.v1.DirectDebitEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

class MandatesIntegrationTest extends KafkaTestBase {

  @MockBean
  private TransactionMonitoringClient transactionMonitoringClient;

  @SpyBean
  private PaymentsNotificationsListener paymentsNotificationsListener;

  @Captor
  private ArgumentCaptor<ConsumerRecord<String, DirectDebitEvent>> consumerRecordArgumentCaptor;

  @Captor
  private ArgumentCaptor<SetupMandates> setupMandatesArgumentCaptor;

  private Producer<String, DirectDebitEvent> directDebitEventProducer;
  private Consumer<String, DirectDebitEvent> directDebitEventConsumer;

  @Value("${spring.kafka.consumer.payments-notifications-topic}")
  private String topic;

  @BeforeEach
  @Override
  public void initTest() {
    directDebitEventProducer = new KafkaProducer<>(producerProps);

    directDebitEventConsumer = new DefaultKafkaConsumerFactory<String, DirectDebitEvent>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    directDebitEventConsumer
        .subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("PaymentsNotificationsListener");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @Override
  public void resetTest() {
    directDebitEventConsumer.close();
    directDebitEventProducer.close();
  }

  /**
   * @throws TransactionMonitoringException exception
   */
  @Test
  void testMandatesSuccess() throws TransactionMonitoringException {
    DirectDebitEvent directDebitEvent = DirectDebitEvent.newBuilder()
        .setAction("SETUP")
        .setBacsDdMandateId("Test")
        .setBacsDDMandateRef("bacsDDMandateRef")
        .setCreditorAccountName("creditorAccountName")
        .setCreditorAccountNumber("Test")
        .setCreditorSortCode("Test")
        .setCurrency("Test")
        .setDateLastProcessed("Test")
        .setDateReceived("Test")
        .setDebitorAccountNumber("Test")
        .setDebitorAccountName("Test")
        .setDebitorSortCode("Test")
        .setDirectDebitKey("directDebitKey")
        .setIdempotencyKey("Test")
        .setPartyKey("partyKeyTest")
        .setProcessorMandateId("Test")
        .setTenantKey("Test")
        .build();

    processKafkaMessage(directDebitEvent);
    Mockito.verify(paymentsNotificationsListener, times(1))
        .handleDirectDebitEvent(consumerRecordArgumentCaptor.capture(), any());
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendMandatesEvent(setupMandatesArgumentCaptor.capture());

    ConsumerRecord<String, DirectDebitEvent> receivedRecord = consumerRecordArgumentCaptor
        .getValue();
    DirectDebitEvent receivedEvent = receivedRecord.value();
    assertEquals("SETUP", receivedEvent.getAction());
    assertEquals(receivedEvent.getPartyKey(), directDebitEvent.getPartyKey());
    assertEquals(receivedEvent.getBacsDDMandateRef(), directDebitEvent.getBacsDDMandateRef());
    assertEquals(receivedEvent.getCreditorAccountName(),
        directDebitEvent.getCreditorAccountName());
    assertEquals(receivedEvent.getDirectDebitKey(), directDebitEvent.getDirectDebitKey());
    assertEquals(receivedEvent.getAction(), directDebitEvent.getAction());

    SetupMandates setupMandates = setupMandatesArgumentCaptor.getValue();
    assertThat(setupMandates.getAction().name()).isEqualTo(BeneficiaryAction.SETUP.name());
    assertEquals(setupMandates.getPartyKey(), directDebitEvent.getPartyKey());
    assertEquals(setupMandates.getReference(), directDebitEvent.getBacsDDMandateRef());
    assertEquals(setupMandates.getAccountName(), directDebitEvent.getCreditorAccountName());
    assertEquals(setupMandates.getDirectDebitKey(), directDebitEvent.getDirectDebitKey());
    assertEquals(setupMandates.getAction().toString(), directDebitEvent.getAction());

  }

  private void processKafkaMessage(DirectDebitEvent directDebitEvent) {
    directDebitEventProducer.send(new ProducerRecord<>(topic, null, directDebitEvent));
    KafkaTestUtils.getSingleRecord(directDebitEventConsumer, topic);
    waitForKafkaListenerToProcess();
  }

  private void isExecutionCompleted() throws TransactionMonitoringException {
    Mockito.verify(paymentsNotificationsListener, times(1))
        .handleDirectDebitEvent(consumerRecordArgumentCaptor.capture(), any());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendMandatesEvent(setupMandatesArgumentCaptor.capture());

    assertNotNull(consumerRecordArgumentCaptor.getValue());
    assertNotNull(setupMandatesArgumentCaptor.getValue());
  }

  public void waitForKafkaListenerToProcess() {
    waitAtMost(1, TimeUnit.SECONDS).alias(this.topic).untilAsserted(this::isExecutionCompleted);
  }
}
