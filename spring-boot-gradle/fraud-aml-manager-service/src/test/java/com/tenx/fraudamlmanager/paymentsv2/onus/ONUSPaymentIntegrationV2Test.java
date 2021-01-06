package com.tenx.fraudamlmanager.paymentsv2.onus;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraud.payments.AccountDetails;
import com.tenx.fraud.payments.Address;
import com.tenx.fraud.payments.Amount;
import com.tenx.fraud.payments.onus.ONUSPaymentFraudCheck;
import com.tenx.fraud.payments.onus.ONUSTransaction;
import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.api.DomesticPaymentEventListenerV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.infrastructure.OnUsTransactionMonitoringClientV2;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
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


class ONUSPaymentIntegrationV2Test extends KafkaTestBase {

  @MockBean
  private OnUsTransactionMonitoringClientV2 transactionMonitoringClient;

  @SpyBean
  private DomesticPaymentEventListenerV2 domesticPaymentEventListener;

  @Captor
  private ArgumentCaptor<ONUSPaymentFraudCheck> onUsPaymentArgumentCaptor;

  @Captor
  private ArgumentCaptor<OnUsPaymentV2> onUsPaymentV2ArgumentCaptor;

  private Producer<String, ONUSPaymentFraudCheck> onUsPaymentFraudCheckProducer;
  private Consumer<String, ONUSPaymentFraudCheck> onUsPaymentFraudCheckConsumer;

  @Value("${spring.kafka.consumer.fps-fraud-check-request-event-topic}")
  private String requestTopic;

  @Override
  @BeforeEach
  public void initTest() {

    eventId = "onUsEventV2";

    onUsPaymentFraudCheckProducer = new KafkaProducer<>(producerProps);

    onUsPaymentFraudCheckConsumer = new DefaultKafkaConsumerFactory<String, ONUSPaymentFraudCheck>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    onUsPaymentFraudCheckConsumer
        .subscribe(new ArrayList<>(List.of(requestTopic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("domesticPaymentEventV2");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @AfterEach
  @Override
  public void resetTest() {
    onUsPaymentFraudCheckConsumer.close();
    onUsPaymentFraudCheckProducer.close();
    eventId = null;
  }

  @Test
  void testOnUsPaymentV2HappyPath() {

    ONUSPaymentFraudCheck payload = ONUSPaymentFraudCheck.newBuilder()
        .setTransaction(ONUSTransaction.newBuilder()
            .setCreditorTransactionId("CTId")
            .setDebtorTransactionId("DTId")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2020-04-05T19:52:13.584+0000")
            .setMessageDate("2020-04-05T19:52:13.584+0000")
            .setStatus("st")
            .setPaymentTypeInformation("SOP:30")
            .setReference("")
            .setTags(new ArrayList<>())
            .setPaymentTypeInformation("PI")
            .setInstructionId("IId").build())
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creACN", "creBID", "cnn", new Address("test"), "creACN"))
        .setDebtor(new AccountDetails("debBID", "debACN", "debBID", "cnn", new Address("test"), "rf"))
        .setCreditorPartyKey("CPK")
        .setDebtorPartyKey("DPK")
        .build();

    onUsPaymentFraudCheckProducer
        .send(new ProducerRecord<>(requestTopic, null, payload));

    KafkaTestUtils.getSingleRecord(onUsPaymentFraudCheckConsumer, requestTopic);

    waitAtMost(1, TimeUnit.SECONDS).alias(this.requestTopic).untilAsserted(this::isExecutionCompleted);

    ONUSPaymentFraudCheck receivedEvent = onUsPaymentArgumentCaptor.getValue();
    assertEquals(payload.getSchema(), receivedEvent.getSchema());
    assertEquals(payload.getTransaction().getAmount(), receivedEvent.getTransaction().getAmount());
    assertEquals(payload.getBalanceBefore().getCurrency(), receivedEvent.getBalanceBefore().getCurrency());

    OnUsPaymentV2 onUsPaymentV3 = onUsPaymentV2ArgumentCaptor.getValue();

    assertEquals(payload.getTransaction().getDebtorTransactionId(), onUsPaymentV3.getTransactionId());
    assertEquals(payload.getCreditor().getName(), onUsPaymentV3.getCreditorName());

  }

  private void isExecutionCompleted()
      throws TransactionMonitoringException, ParseException, OnUsTransactionMonitoringExceptionV2 {
    Mockito.verify(domesticPaymentEventListener, times(1))
        .handleOnUsPayments(onUsPaymentArgumentCaptor.capture(), any());

    Mockito.verify(transactionMonitoringClient, times(1))
        .checkFinCrimeV2(onUsPaymentV2ArgumentCaptor.capture());

    assertNotNull(onUsPaymentV2ArgumentCaptor);
  }
}
