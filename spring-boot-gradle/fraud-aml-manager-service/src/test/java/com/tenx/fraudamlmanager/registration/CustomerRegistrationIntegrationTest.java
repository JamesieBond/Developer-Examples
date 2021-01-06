package com.tenx.fraudamlmanager.registration;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.registration.api.CustomerRegistrationEventListener;
import com.tenx.fraudamlmanager.registration.infrastructure.RegistrationDetails;
import com.tenx.security.forgerockfacade.resource.CustomerRegistration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
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

class CustomerRegistrationIntegrationTest extends KafkaTestBase {

  @MockBean
  private TransactionMonitoringClient transactionMonitoringClient;

  @SpyBean
  private CustomerRegistrationEventListener customerRegistrationEventListener;

  @Captor
  private ArgumentCaptor<ConsumerRecord<String, CustomerRegistration>> consumerRecordArgumentCaptor;

  @Captor
  private ArgumentCaptor<RegistrationDetails> customerRegistrationDetailsArgumentCaptor;


  private Producer<String, CustomerRegistration> customerRegistrationProducer;
  private Consumer<String, CustomerRegistration> customerRegistrationConsumer;

  @Value("${spring.kafka.consumer.kafka-identity-customer-registration-v1-topic}")
  private String topic;

  @BeforeEach
  public void initTest() {
    customerRegistrationProducer = new KafkaProducer<>(producerProps);

    customerRegistrationConsumer = new DefaultKafkaConsumerFactory<String, CustomerRegistration>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    customerRegistrationConsumer
        .subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("CustomerRegistrationEventListener");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @Override
  @AfterEach
  public void resetTest() {
    customerRegistrationConsumer.close();
    customerRegistrationProducer.close();
  }

  @Test
  void testCustomerRegistration()
      throws TransactionMonitoringException {

    CustomerRegistration customerRegistrationEvent = CustomerRegistration.newBuilder()
        .setPartyKey("partyKey")
        .setPasscodeProvided(true)
        .setPasswordProvided(true)
        .setDeviceId("deviceId")
        .setTimestamp(ZonedDateTime.now().toString())
        .build();

    processKafkaMessage(customerRegistrationEvent);
    Mockito.verify(customerRegistrationEventListener, times(1))
        .handleCustomerRegistrationEvent(consumerRecordArgumentCaptor.capture(), any());

    CustomerRegistration receivedEvent = consumerRecordArgumentCaptor.getValue().value();
    assertEquals(customerRegistrationEvent.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(customerRegistrationEvent.getPasscodeProvided(),
        receivedEvent.getPasscodeProvided());
    assertEquals(customerRegistrationEvent.getPasswordProvided(),
        receivedEvent.getPasswordProvided());
    assertEquals(customerRegistrationEvent.getDeviceId(), receivedEvent.getDeviceId());
    assertEquals(customerRegistrationEvent.getTimestamp(),
        receivedEvent.getTimestamp());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendCustomerRegistrationEvent(customerRegistrationDetailsArgumentCaptor.capture());

    RegistrationDetails capturedDetails = customerRegistrationDetailsArgumentCaptor.getValue();

    assertEquals(capturedDetails.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(capturedDetails.getPasscodeProvided(), receivedEvent.getPasscodeProvided());
    assertEquals(capturedDetails.getPasswordProvided(), receivedEvent.getPasswordProvided());

  }

  private void processKafkaMessage(CustomerRegistration customerRegistration) {
    customerRegistrationProducer
        .send(new ProducerRecord<>(topic, null, customerRegistration));
    KafkaTestUtils.getSingleRecord(customerRegistrationConsumer, topic);
    waitForKafkaListenerToProcess();
  }

  private void isExecutionCompleted() throws TransactionMonitoringException {
    Mockito.verify(customerRegistrationEventListener, times(1))
        .handleCustomerRegistrationEvent(consumerRecordArgumentCaptor.capture(), any());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendCustomerRegistrationEvent(customerRegistrationDetailsArgumentCaptor.capture());
    assertNotNull(consumerRecordArgumentCaptor);
    assertNotNull(customerRegistrationDetailsArgumentCaptor);
  }

  public void waitForKafkaListenerToProcess() {
    waitAtMost(1, TimeUnit.SECONDS).alias(this.topic).untilAsserted(this::isExecutionCompleted);
  }

}
