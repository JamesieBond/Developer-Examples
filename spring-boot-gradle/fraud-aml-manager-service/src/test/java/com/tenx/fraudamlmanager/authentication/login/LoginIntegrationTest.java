package com.tenx.fraudamlmanager.authentication.login;

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.authentication.loginattempts.api.LoginAttemptsEventListener;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.AuthMethod;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.AuthOutcome;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttempts;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.Login;
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


class LoginIntegrationTest extends KafkaTestBase {


  @MockBean
  private TransactionMonitoringClient transactionMonitoringClient;

  @SpyBean
  private LoginAttemptsEventListener loginAttemptsEventListener;

  @Mock
  private Acknowledgment acknowledgment;

  @Captor
  private ArgumentCaptor<ConsumerRecord<String, Login>> consumerRecordArgumentCaptor;

  @Captor
  private ArgumentCaptor<LoginAttempts> loginAttemptsCaptor;

  private Producer<String, Login> loginProducer;
  private Consumer<String, Login> loginConsumer;

  @Value("${spring.kafka.consumer.identity-login-v1-topic}")
  private String topic;

  private static Stream<Arguments> payloadsHappyPath() {
    Login loginSuccess = Login.newBuilder()
        .setAuthOutcome("SUCCESS")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("PASSCODE")
        .setPartyKey("PartyKey")
        .setTimestamp("timestamp")
        .build();

    Login loginFailed = Login.newBuilder().setAuthOutcome("FAILED")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("BIOMETRIC")
        .setPartyKey("PartyKey")
        .setTimestamp("timestamp")
        .build();

    Login loginFailourReasonNull = Login.newBuilder()
        .setAuthOutcome("SUCCESS")
        .setDeviceId("DEVICEID")
        .setLoginMethod("PASSCODE")
        .setPartyKey("PartyKey")
        .setTimestamp("timestamp")
        .build();

    Login loginFailourReasonEmpty = Login.newBuilder()
        .setAuthOutcome("SUCCESS")
        .setFailureReason("")
        .setDeviceId("DEVICEID")
        .setLoginMethod("PASSCODE")
        .setPartyKey("PartyKey")
        .setTimestamp("timestamp")
        .build();

    LoginAttempts loginDetailsSucces = new LoginAttempts(
        "PartyKey", AuthOutcome.SUCCESS, AuthMethod.PASSCODE, "FailureReason");

    LoginAttempts loginDetailsFailed = new LoginAttempts(
        "PartyKey", AuthOutcome.FAILED, AuthMethod.BIOMETRIC, "FailureReason");

    LoginAttempts loginDetailsFailourReasonNull = new LoginAttempts(
        "PartyKey", AuthOutcome.SUCCESS, AuthMethod.PASSCODE, null);

    LoginAttempts loginDetailsFailourReasonEmpty = new LoginAttempts(
        "PartyKey", AuthOutcome.SUCCESS, AuthMethod.PASSCODE, "");

    return Stream.of(
        Arguments.of(loginSuccess, loginDetailsSucces),
        Arguments.of(loginFailed, loginDetailsFailed),
        Arguments.of(loginFailourReasonNull, loginDetailsFailourReasonNull),
        Arguments.of(loginFailourReasonEmpty, loginDetailsFailourReasonEmpty)
    );
  }

  private static Stream<Arguments> invalidPayloads() {

    Login loginPkEmpty = Login.newBuilder()
        .setAuthOutcome("FAILED")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("PASSCODE")
        .setPartyKey("")
        .setTimestamp("timestamp")
        .build();

    Login loginPkNull = Login.newBuilder()
        .setAuthOutcome("FAILED")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("PASSCODE")
        .setTimestamp("timestamp")
        .build();

    LoginAttempts loginDetailsPKempty = new LoginAttempts(
        "", AuthOutcome.FAILED, AuthMethod.PASSCODE, "FailureReason");

    LoginAttempts loginDetailsPKNull = new LoginAttempts(
        null, AuthOutcome.FAILED, AuthMethod.PASSCODE, "FailureReason");

    return Stream.of(
        Arguments.of(loginPkEmpty, loginDetailsPKempty),
        Arguments.of(loginPkNull, loginDetailsPKNull)
    );
  }

  private static Stream<Arguments> tMANotCalledPayloads() {

    Login loginDetailsAuthOutComeempty = Login.newBuilder()
        .setAuthOutcome("")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("PASSCODE")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    Login loginDetailsAuthOutComeNull = Login.newBuilder()
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("PASSCODE")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    Login loginDetailsAuthOutComeRandomString = Login.newBuilder()
        .setAuthOutcome("RANDOMSTRING")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("PASSCODE")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    Login loginDetailsAuthMthodEmpty = Login.newBuilder()
        .setAuthOutcome("FAILED")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    Login loginDetailsAuthMthodNull = Login.newBuilder()
        .setAuthOutcome("FAILED")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    Login loginDetailsAuthMthodRandomString = Login.newBuilder()
        .setAuthOutcome("FAILED")
        .setDeviceId("DEVICEID")
        .setFailureReason("FailureReason")
        .setLoginMethod("RANDOMSTRING")
        .setPartyKey("partyKey")
        .setTimestamp("timestamp")
        .build();

    return Stream.of(
        Arguments.of(loginDetailsAuthOutComeempty),
        Arguments.of(loginDetailsAuthOutComeNull),
        Arguments.of(loginDetailsAuthOutComeRandomString),
        Arguments.of(loginDetailsAuthMthodEmpty),
        Arguments.of(loginDetailsAuthMthodNull),
        Arguments.of(loginDetailsAuthMthodRandomString)

    );
  }

  @Override
  @BeforeEach
  public void initTest() {
    loginProducer = new KafkaProducer<>(producerProps);

    loginConsumer = new DefaultKafkaConsumerFactory<String, Login>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    loginConsumer
        .subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("LoginAttemptsEventListener");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @Override
  @AfterEach
  public void resetTest() {
    loginProducer.close();
    loginConsumer.close();
  }

  @ParameterizedTest
  @MethodSource("payloadsHappyPath")
  void loginAttemptsE2ETestHappyPath(Login payload, LoginAttempts output)
      throws TransactionMonitoringException {

    processKafkaMessage(payload);

    Mockito.verify(loginAttemptsEventListener, times(1))
        .handleLoginAttemptsEvent(consumerRecordArgumentCaptor.capture(), any());

    Login receivedEvent = consumerRecordArgumentCaptor.getValue().value();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getAuthOutcome(), receivedEvent.getAuthOutcome());
    assertEquals(payload.getFailureReason(), receivedEvent.getFailureReason());
    assertEquals(payload.getLoginMethod(), receivedEvent.getLoginMethod());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendLoginAttemptsEvent(loginAttemptsCaptor.capture());

    LoginAttempts capturedDetails = loginAttemptsCaptor.getValue();

    assertEquals(capturedDetails.getPartyKey(), output.getPartyKey());
    assertEquals(capturedDetails.getAuthOutcome(), output.getAuthOutcome());
    assertEquals(capturedDetails.getAuthMethod(), output.getAuthMethod());
    assertEquals(capturedDetails.getFailureReason(), output.getFailureReason());
  }

  @ParameterizedTest
  @MethodSource("invalidPayloads")
  void loginAttemptsE2EInvaildPayloads(Login payload, LoginAttempts output)
      throws TransactionMonitoringException {
    processKafkaMessage(payload);

    Mockito.verify(loginAttemptsEventListener, times(1))
        .handleLoginAttemptsEvent(consumerRecordArgumentCaptor.capture(), any());

    Login receivedEvent = consumerRecordArgumentCaptor.getValue().value();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getAuthOutcome(), receivedEvent.getAuthOutcome());
    assertEquals(payload.getFailureReason(), receivedEvent.getFailureReason());
    assertEquals(payload.getLoginMethod(), receivedEvent.getLoginMethod());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendLoginAttemptsEvent(loginAttemptsCaptor.capture());

    LoginAttempts capturedDetails = loginAttemptsCaptor.getValue();

    assertEquals(capturedDetails.getPartyKey(), output.getPartyKey());
    assertEquals(capturedDetails.getAuthOutcome(), output.getAuthOutcome());
    assertEquals(capturedDetails.getAuthMethod(), output.getAuthMethod());
    assertEquals(capturedDetails.getFailureReason(), output.getFailureReason());
  }

  @ParameterizedTest
  @MethodSource("tMANotCalledPayloads")
  void loginAttemptsE2ETMANotCalled(Login payload)
      throws TransactionMonitoringException {

    ConsumerRecord<String, Login> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", payload);

    loginAttemptsEventListener.handleLoginAttemptsEvent(consumerRecord, acknowledgment);

    Mockito.verify(transactionMonitoringClient, times(0))
        .sendLoginAttemptsEvent(loginAttemptsCaptor.capture());
  }

  private void processKafkaMessage(Login login) {
    loginProducer
        .send(new ProducerRecord<>(topic, null, login));
    KafkaTestUtils.getSingleRecord(loginConsumer, topic);
    waitForKafkaListenerToProcess();
  }

  private boolean isExecutionCompleted() throws TransactionMonitoringException {
    Mockito.verify(loginAttemptsEventListener, times(1))
        .handleLoginAttemptsEvent(consumerRecordArgumentCaptor.capture(), any());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendLoginAttemptsEvent(loginAttemptsCaptor.capture());

    return consumerRecordArgumentCaptor.getValue() != null
        && loginAttemptsCaptor.getValue() != null;
  }

  public void waitForKafkaListenerToProcess() {
    given().alias(this.topic).await().until(this::isExecutionCompleted);
  }
}
