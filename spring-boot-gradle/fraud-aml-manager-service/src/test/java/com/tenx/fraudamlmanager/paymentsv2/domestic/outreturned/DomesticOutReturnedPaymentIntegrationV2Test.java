package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturned;

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraud.payments.AccountDetails;
import com.tenx.fraud.payments.Address;
import com.tenx.fraud.payments.Amount;
import com.tenx.fraud.payments.onus.FPSOutReturnTransaction;
import com.tenx.fraud.payments.onus.FPSOutboundReturnPaymentFraudCheck;
import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.api.DomesticPaymentEventListenerV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.infrastructure.DomesticOutReturnTransactionMonitoringClientV2;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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
import org.mockito.exceptions.base.MockitoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;


class DomesticOutReturnedPaymentIntegrationV2Test extends KafkaTestBase {

  @MockBean
  private DomesticOutReturnTransactionMonitoringClientV2 domesticOutReturnTransactionMonitoringClientV2;

  @SpyBean
  private DomesticPaymentEventListenerV2 domesticPaymentEventListener;

  @Captor
  private ArgumentCaptor<FPSOutboundReturnPaymentFraudCheck> fpsOutboundReturnPaymentFraudCheckArgumentCaptor;

  @Captor
  private ArgumentCaptor<DomesticOutReturnPaymentV2> domesticOutReturnPaymentV2ArgumentCaptor;

  private Producer<String, FPSOutboundReturnPaymentFraudCheck> fpsOutboundReturnPaymentFraudCheckProducer;
  private Consumer<String, FPSOutboundReturnPaymentFraudCheck> fpsOutboundReturnPaymentFraudCheckConsumer;

  @Value("${spring.kafka.consumer.fps-fraud-check-request-event-topic}")
  private String requestTopic;

  @Override
  @BeforeEach
  public void initTest() {

    eventId = "domesticOutReturnEventV2";

    fpsOutboundReturnPaymentFraudCheckProducer = new KafkaProducer<>(producerProps);

    fpsOutboundReturnPaymentFraudCheckConsumer = new DefaultKafkaConsumerFactory<String, FPSOutboundReturnPaymentFraudCheck>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    fpsOutboundReturnPaymentFraudCheckConsumer
        .subscribe(new ArrayList<>(List.of(requestTopic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("domesticPaymentEventV2");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @AfterEach
  @Override
  public void resetTest() {
    fpsOutboundReturnPaymentFraudCheckProducer.close();
    fpsOutboundReturnPaymentFraudCheckConsumer.close();
    eventId = null;
  }

  @Test
  void testDomesticOutboundReturnPaymentV2HappyPath() {

    FPSOutboundReturnPaymentFraudCheck payload = FPSOutboundReturnPaymentFraudCheck.newBuilder()
        .setTransaction(FPSOutReturnTransaction.newBuilder()
            .setId("TSID")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2020-04-05T19:52:13.584+0000")
            .setMessageDate("2020-04-05T19:52:13.584+0000")
            .setStatus("st")
            .setReference("rf")
            .setPaymentTypeInformation("SOP:30")
            .setTags(new ArrayList<>())
            .build())
        .setPartyKey("partyKey")
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creACN", "creBID", "cnn", new Address("test"), "creACN"))
        .setDebtor(new AccountDetails("debBID", "debACN", "debBID", "cnn", new Address("test"), ""))
        .build();

    fpsOutboundReturnPaymentFraudCheckProducer
        .send(new ProducerRecord<>(requestTopic, null, payload));

    KafkaTestUtils.getSingleRecord(fpsOutboundReturnPaymentFraudCheckConsumer, requestTopic);

    given().alias(this.requestTopic).ignoreException(MockitoException.class).await().until(this::isExecutionCompleted);

    FPSOutboundReturnPaymentFraudCheck receivedEvent = fpsOutboundReturnPaymentFraudCheckArgumentCaptor.getValue();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getSchema(), receivedEvent.getSchema());
    assertEquals(payload.getTransaction().getAmount(), receivedEvent.getTransaction().getAmount());
    assertEquals(payload.getBalanceBefore().getCurrency(), receivedEvent.getBalanceBefore().getCurrency());

    DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2 = domesticOutReturnPaymentV2ArgumentCaptor.getValue();

    assertEquals(payload.getPartyKey(), domesticOutReturnPaymentV2.getPartyKey());
    assertEquals(payload.getTransaction().getReference(), domesticOutReturnPaymentV2.getTransactionReference());
    assertEquals(payload.getTransaction().getId(), domesticOutReturnPaymentV2.getTransactionId());
    assertEquals(payload.getCreditor().getName(), domesticOutReturnPaymentV2.getCreditorName());

  }

  private boolean isExecutionCompleted() throws DomesticOutReturnTransactionMonitoringExceptionV2, ParseException,
      TransactionMonitoringException {
    Mockito.verify(domesticPaymentEventListener, times(1))
        .handleOutboundReturnPaymentEvent(fpsOutboundReturnPaymentFraudCheckArgumentCaptor.capture(), any());

    Mockito.verify(domesticOutReturnTransactionMonitoringClientV2, times(1))
        .postReturnPayment(domesticOutReturnPaymentV2ArgumentCaptor.capture());

    return domesticOutReturnPaymentV2ArgumentCaptor.getValue() != null;
  }

}
