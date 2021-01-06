package com.tenx.fraudamlmanager.authentication.reset;

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.authentication.reset.api.IdentityAccountResetEventListener;
import com.tenx.fraudamlmanager.authentication.reset.infrastructure.AuthReset;
import com.tenx.fraudamlmanager.authentication.reset.infrastructure.IdentityAccountReset;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.AccountResetNotification;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

class AccountResetIntegrationTest extends KafkaTestBase {

  @MockBean
  private TransactionMonitoringClient transactionMonitoringClient;

  @SpyBean
  private IdentityAccountResetEventListener identityAccountResetEventListener;

  @Captor
  private ArgumentCaptor<ConsumerRecord<String, AccountResetNotification>> consumerRecordArgumentCaptor;

  @Captor
  private ArgumentCaptor<AuthReset> authResetArgumentCaptor;

  @Mock
  private Acknowledgment acknowledgment;

  private Producer<String, AccountResetNotification> accountResetNotificationProducer;
  private Consumer<String, AccountResetNotification> accountResetNotificationConsumer;

  @Value("${spring.kafka.consumer.identity-account-reset-notification-topic}")
  private String topic;

  @BeforeEach
  @Override
  public void initTest() {
    accountResetNotificationProducer = new KafkaProducer<>(producerProps);

    accountResetNotificationConsumer = new DefaultKafkaConsumerFactory<String, AccountResetNotification>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    accountResetNotificationConsumer
        .subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("IdentityAccountResetEventListener");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  private static Stream<Arguments> payloadsHappyPath() {
    AccountResetNotification accountResetNotificationSuccess = AccountResetNotification.newBuilder()
        .setPartyKey("PARTYKEY")
        .setCheckResult("PASSED")
        .setTenantKey("10000")
        .setTimestamp("Timestamp")
        .setTransactionId("TransactionId")
        .build();

    AccountResetNotification accountResetNotificationFailed = AccountResetNotification.newBuilder()
        .setPartyKey("PARTYKEY")
        .setCheckResult("FAILED")
        .setTenantKey("10000")
        .setTimestamp("Timestamp")
        .setTransactionId("TransactionId")
        .build();

    AuthReset authResetPassed = new AuthReset(
        "PARTYKEY", IdentityAccountReset.PASSED);

    AuthReset authResetFailed = new AuthReset(
        "PARTYKEY", IdentityAccountReset.FAILED);

    AccountResetNotification accountResetNotificationPKEmpty = AccountResetNotification.newBuilder()
        .setPartyKey("")
        .setCheckResult("PASSED")
        .setTenantKey("10000")
        .setTimestamp("Timestamp")
        .setTransactionId("TransactionId")
        .build();

    AuthReset authResetPKEmpty = new AuthReset(
        "", IdentityAccountReset.PASSED);

    return Stream.of(
        Arguments.of(accountResetNotificationSuccess, authResetPassed),
        Arguments.of(accountResetNotificationFailed, authResetFailed),
        Arguments.of(accountResetNotificationPKEmpty, authResetPKEmpty)

    );
  }

  private static Stream<Arguments> tMANotCalledPayloads() {

    AccountResetNotification accountResetNotificationResultEmpty = AccountResetNotification.newBuilder()
        .setCheckResult("")
        .setPartyKey("PARTYKEY")
        .setTenantKey("10000")
        .setTimestamp("Timestamp")
        .setTransactionId("TransactionId")
        .build();

    AccountResetNotification accountResetNotificationResultRandomString = AccountResetNotification.newBuilder()
        .setCheckResult("RANDOMSTRING")
        .setPartyKey("PARTYKEY")
        .setTenantKey("10000")
        .setTimestamp("Timestamp")
        .setTransactionId("TransactionId")
        .build();

    return Stream.of(
        Arguments.of(accountResetNotificationResultEmpty),
        Arguments.of(accountResetNotificationResultRandomString)
    );
  }

  @Override
  @AfterEach
  public void resetTest() {
    accountResetNotificationProducer.close();
    accountResetNotificationConsumer.close();
  }

  private void processKafkaMessage(AccountResetNotification accountResetNotification) {
    accountResetNotificationProducer
        .send(new ProducerRecord<>(topic, null, accountResetNotification));
    KafkaTestUtils.getSingleRecord(accountResetNotificationConsumer, topic);
    waitForKafkaListenerToProcess();
  }

  private boolean isExecutionCompleted() throws TransactionMonitoringException {
    Mockito.verify(identityAccountResetEventListener, times(1))
        .handleIdentityAccountResetEvent(consumerRecordArgumentCaptor.capture(), any());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendIdentityAccountResetEvent(authResetArgumentCaptor.capture());

    return consumerRecordArgumentCaptor.getValue() != null
        && authResetArgumentCaptor.getValue() != null;
  }

  public void waitForKafkaListenerToProcess() {
    given().alias(this.topic).await().until(this::isExecutionCompleted);
  }

  @ParameterizedTest
  @MethodSource("payloadsHappyPath")
  void AccountResetE2ETestHappyPath(AccountResetNotification payload, AuthReset output)
      throws TransactionMonitoringException {

    processKafkaMessage(payload);

    Mockito.verify(identityAccountResetEventListener, times(1))
        .handleIdentityAccountResetEvent(consumerRecordArgumentCaptor.capture(), any());
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendIdentityAccountResetEvent(authResetArgumentCaptor.capture());

    ConsumerRecord<String, AccountResetNotification> receivedRecord = consumerRecordArgumentCaptor
        .getValue();
    AccountResetNotification receivedEvent = receivedRecord.value();

    assertEquals(receivedEvent.getPartyKey(), payload.getPartyKey());
    assertEquals(receivedEvent.getCheckResult(), payload.getCheckResult());
    assertEquals(receivedEvent.getTenantKey(), payload.getTenantKey());
    assertEquals(receivedEvent.getTransactionId(), payload.getTransactionId());
    assertEquals(receivedEvent.getTimestamp(), payload.getTimestamp());

    AuthReset authReset = authResetArgumentCaptor.getValue();

    assertEquals(authReset.getPartyKey(), output.getPartyKey());
    assertEquals(authReset.getResult(), output.getResult());
  }

  @ParameterizedTest
  @MethodSource("tMANotCalledPayloads")
  void AccountResetNotificationAttemptsE2ETMANotCalled(AccountResetNotification payload)
      throws TransactionMonitoringException {

    ConsumerRecord<String, AccountResetNotification> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", payload);

    identityAccountResetEventListener.handleIdentityAccountResetEvent(consumerRecord, acknowledgment);

    Mockito.verify(transactionMonitoringClient, times(0))
        .sendIdentityAccountResetEvent(authResetArgumentCaptor.capture());
  }

}
