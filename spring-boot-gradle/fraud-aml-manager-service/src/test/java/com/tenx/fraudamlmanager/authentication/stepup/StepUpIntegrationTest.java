package com.tenx.fraudamlmanager.authentication.stepup;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.authentication.stepup.api.StepUpEventListener;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpAuthMethod;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpAuthOutcome;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpPayload;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.StepUp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

class StepUpIntegrationTest extends KafkaTestBase {


  @MockBean
  private TransactionMonitoringClient transactionMonitoringClient;

  @SpyBean
  private StepUpEventListener stepUpEventListener;

  @Captor
  private ArgumentCaptor<ConsumerRecord<String, StepUp>> consumerRecordArgumentCaptor;

  @Captor
  private ArgumentCaptor<StepUpPayload> stepUpArgumentCaptor;

  @Mock
  private Acknowledgment acknowledgment;

  private Producer<String, StepUp> stepUpProducer;
  private Consumer<String, StepUp> stepUpConsumer;

  @Value("${spring.kafka.consumer.identity-step-up-v1-topic}")
  private String topic;

  @BeforeEach
  public void initTest() {
    stepUpProducer = new KafkaProducer<>(producerProps);

    stepUpConsumer = new DefaultKafkaConsumerFactory<String, StepUp>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    stepUpConsumer
        .subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("StepUpEventListener");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @Override
  @AfterEach
  public void resetTest() {
    stepUpConsumer.close();
    stepUpProducer.close();
  }

  private static Stream<Arguments> payloadsHappyPath() {
    StepUp stepUpSuccess = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_SUCCESS")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("BIOMETRIC")
        .setPartyKey("PartyKey")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpFailed = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_FAILED")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("PASSCODE")
        .setPartyKey("PartyKey")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpFRnull = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_SUCCESS")
        .setDeviceId("DEVICEID")
        .setAuthMethod("BIOMETRIC")
        .setPartyKey("PartyKey")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpFREmpty = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_SUCCESS")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("")
        .setAuthMethod("BIOMETRIC")
        .setPartyKey("PartyKey")
        .setTimestamp("timestamp")
        .build();

    StepUpPayload stepUpDetailsSucces = new StepUpPayload(
        "PartyKey", StepUpAuthOutcome.STEPUP_SUCCESS, StepUpAuthMethod.BIOMETRIC, "FailureReason");

    StepUpPayload stepUpPayloadFailed = new StepUpPayload(
        "PartyKey", StepUpAuthOutcome.STEPUP_FAILED, StepUpAuthMethod.PASSCODE, "FailureReason");

    StepUpPayload stepUpPayloadFRNull = new StepUpPayload(
        "PartyKey", StepUpAuthOutcome.STEPUP_SUCCESS, StepUpAuthMethod.BIOMETRIC, null);
    StepUpPayload stepUpPayloadFREmpty = new StepUpPayload(
        "PartyKey", StepUpAuthOutcome.STEPUP_SUCCESS, StepUpAuthMethod.BIOMETRIC, "");

    return Stream.of(
        Arguments.of(stepUpSuccess, stepUpDetailsSucces),
        Arguments.of(stepUpFailed, stepUpPayloadFailed),
        Arguments.of(stepUpFRnull, stepUpPayloadFRNull),
        Arguments.of(stepUpFREmpty, stepUpPayloadFREmpty)
    );
  }

  private static Stream<Arguments> inVaildPayloads() {

    StepUp stepUpPkEmpty = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_FAILED")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("PASSCODE")
        .setPartyKey("")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpPkNull = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_FAILED")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("PASSCODE")
        .setTimestamp("timestamp")
        .build();

    StepUpPayload StepUpPayloadPKempty = new StepUpPayload();
    StepUpPayloadPKempty.setPartyKey("");
    StepUpPayloadPKempty.setAuthMethod(StepUpAuthMethod.PASSCODE);
    StepUpPayloadPKempty.setAuthOutcome(StepUpAuthOutcome.STEPUP_FAILED);
    StepUpPayloadPKempty.setFailureReason("FailureReason");

    StepUpPayload StepUpPayloadPKnull = new StepUpPayload(
        null, StepUpAuthOutcome.STEPUP_FAILED, StepUpAuthMethod.PASSCODE, "FailureReason");

    return Stream.of(
        Arguments.of(stepUpPkNull, StepUpPayloadPKnull),
        Arguments.of(stepUpPkEmpty, StepUpPayloadPKempty)
    );
  }

  private static Stream<Arguments> tMANotCalledPayloads() {

    StepUp stepAuthOutComeempty = StepUp.newBuilder()
        .setAuthOutcome("")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("PASSCODE")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpDetailsAuthOutComeNull = StepUp.newBuilder()
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("PASSCODE")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpAuthOutComeRandomString = StepUp.newBuilder()
        .setAuthOutcome("RANDOMSTRING")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("PASSCODE")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpAuthMthodEmpty = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_FAILED")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpAuthMthodNull = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_FAILED")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    StepUp stepUpAuthMthodRandomString = StepUp.newBuilder()
        .setAuthOutcome("STEPUP_FAILED")
        .setDeviceId("DEVICEID")
        .setTransactionFailureReason("FailureReason")
        .setAuthMethod("RANDOMSTRING")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    return Stream.of(
        Arguments.of(stepAuthOutComeempty),
        Arguments.of(stepUpDetailsAuthOutComeNull),
        Arguments.of(stepUpAuthOutComeRandomString),
        Arguments.of(stepUpAuthMthodEmpty),
        Arguments.of(stepUpAuthMthodNull),
        Arguments.of(stepUpAuthMthodRandomString)
    );
  }

  @ParameterizedTest
  @MethodSource("payloadsHappyPath")
  void stepUpE2ETestHappyPath(StepUp payload, StepUpPayload output)
      throws TransactionMonitoringException {

    processKafkaMessage(payload);

    Mockito.verify(stepUpEventListener, times(1))
        .handleStepUpEvent(consumerRecordArgumentCaptor.capture(), any());

    StepUp receivedEvent = consumerRecordArgumentCaptor.getValue().value();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getAuthOutcome(), receivedEvent.getAuthOutcome());
    assertEquals(payload.getAuthMethod(), receivedEvent.getAuthMethod());
    assertEquals(payload.getTransactionFailureReason(),
        receivedEvent.getTransactionFailureReason());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendStepUpEvent(stepUpArgumentCaptor.capture());

    StepUpPayload capturedDetails = stepUpArgumentCaptor.getValue();

    assertEquals(capturedDetails.getPartyKey(), output.getPartyKey());
    assertEquals(capturedDetails.getAuthOutcome(), output.getAuthOutcome());
    assertEquals(capturedDetails.getAuthMethod(), output.getAuthMethod());
    assertEquals(capturedDetails.getFailureReason(), output.getFailureReason());
  }

  private void processKafkaMessage(StepUp stepUp) {
    stepUpProducer
        .send(new ProducerRecord<>(topic, null, stepUp));
    KafkaTestUtils.getSingleRecord(stepUpConsumer, topic);
    waitForKafkaListenerToProcess();
  }

  @ParameterizedTest
  @MethodSource("inVaildPayloads")
  void stepUpE2ETestInvaildPayload(StepUp payload, StepUpPayload output)
      throws TransactionMonitoringException {

    processKafkaMessage(payload);

    Mockito.verify(stepUpEventListener, times(1))
        .handleStepUpEvent(consumerRecordArgumentCaptor.capture(), any());

    StepUp receivedEvent = consumerRecordArgumentCaptor.getValue().value();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getAuthOutcome(), receivedEvent.getAuthOutcome());
    assertEquals(payload.getAuthMethod(), receivedEvent.getAuthMethod());
    assertEquals(payload.getTransactionFailureReason(),
        receivedEvent.getTransactionFailureReason());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendStepUpEvent(stepUpArgumentCaptor.capture());

    StepUpPayload capturedDetails = stepUpArgumentCaptor.getValue();

    assertEquals(capturedDetails.getPartyKey(), output.getPartyKey());
    assertEquals(capturedDetails.getAuthOutcome(), output.getAuthOutcome());
    assertEquals(capturedDetails.getAuthMethod(), output.getAuthMethod());
    assertEquals(capturedDetails.getFailureReason(), output.getFailureReason());
  }

  public void waitForKafkaListenerToProcess() {
    waitAtMost(1, TimeUnit.SECONDS).alias(this.topic).untilAsserted(this::isExecutionCompleted);
  }

  private void isExecutionCompleted() throws TransactionMonitoringException {
    Mockito.verify(stepUpEventListener, times(1))
        .handleStepUpEvent(consumerRecordArgumentCaptor.capture(), any());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendStepUpEvent(stepUpArgumentCaptor.capture());

    assertNotNull(consumerRecordArgumentCaptor.getValue());
    assertNotNull(stepUpArgumentCaptor.getValue());
  }

  @ParameterizedTest
  @MethodSource("tMANotCalledPayloads")
  void stepUpE2ETMANotCalled(StepUp payload)
      throws TransactionMonitoringException {

    ConsumerRecord<String, StepUp> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", payload);

    stepUpEventListener.handleStepUpEvent(consumerRecord, acknowledgment);

    Mockito.verify(transactionMonitoringClient, times(0))
        .sendStepUpEvent(stepUpArgumentCaptor.capture());
  }
}
