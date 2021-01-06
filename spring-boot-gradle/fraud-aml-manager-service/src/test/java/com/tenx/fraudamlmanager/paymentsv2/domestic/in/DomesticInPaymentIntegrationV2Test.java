package com.tenx.fraudamlmanager.paymentsv2.domestic.in;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraud.payments.AccountDetails;
import com.tenx.fraud.payments.Address;
import com.tenx.fraud.payments.Agent;
import com.tenx.fraud.payments.Amount;
import com.tenx.fraud.payments.ChargesInformation;
import com.tenx.fraud.payments.fpsin.FPSInTransaction;
import com.tenx.fraud.payments.fpsin.FPSInboundPaymentFraudCheck;
import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.api.DomesticPaymentEventListenerV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInTransactionMonitoringExceptionV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.infrastructure.DomesticInTransactionMonitoringClientV2;
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

class DomesticInPaymentIntegrationV2Test extends KafkaTestBase {

  @MockBean
  private DomesticInTransactionMonitoringClientV2 transactionMonitoringClient;

  @SpyBean
  private DomesticPaymentEventListenerV2 domesticPaymentEventListener;

  @Captor
  private ArgumentCaptor<FPSInboundPaymentFraudCheck> fpsInboundPaymentArgumentCaptor;

  @Captor
  private ArgumentCaptor<com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2> domesticInPaymentV2ArgumentCaptor;

  private Producer<String, FPSInboundPaymentFraudCheck> fpsInboundPaymentFraudCheckProducer;
  private Consumer<String, FPSInboundPaymentFraudCheck> fpsInboundPaymentFraudCheckConsumer;

  @Value("${spring.kafka.consumer.fps-fraud-check-request-event-topic}")
  private String requestTopic;

  @Override
  @BeforeEach
  public void initTest() {

    eventId = "domesticInEventV2";

    fpsInboundPaymentFraudCheckProducer = new KafkaProducer<>(producerProps);

    fpsInboundPaymentFraudCheckConsumer = new DefaultKafkaConsumerFactory<String, FPSInboundPaymentFraudCheck>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    fpsInboundPaymentFraudCheckConsumer
        .subscribe(new ArrayList<>(List.of(requestTopic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("domesticPaymentEventV2");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @AfterEach
  @Override
  public void resetTest() {
    fpsInboundPaymentFraudCheckConsumer.close();
    fpsInboundPaymentFraudCheckProducer.close();
    eventId = null;
  }

  @Test
  void testDomesticInboundPaymentV2HappyPath() {

    FPSInboundPaymentFraudCheck payload = FPSInboundPaymentFraudCheck.newBuilder()
        .setTransaction(FPSInTransaction.newBuilder()
            .setId("TSID")
            .setAmount(new Amount("eu", "50", "usd", "50"))
            .setDate("2020-04-05T19:52:13.584+0000")
            .setMessageDate("2020-04-05T19:52:13.584+0000")
            .setStatus("st")
            .setPaymentTypeInformation("SOP:30")
            .setInterbankSettlementDate("2020-04-05T19:52:13.584+0000")
            .setEndToEndIdentification("endToEnd")
            .setPaymentIdentification("paymentIdentification")
            .setInstructingAgentMemberIdentification("instructingAgentMemberIdentification")
            .setOriginatingCreditInstitution("CreditInstitution")
            .setChargeInformation(ChargesInformation.newBuilder()
                .setAmount(Amount.newBuilder()
                    .setValue("10000")
                    .setBaseValue("10000")
                    .setCurrency("EUR")
                    .setBaseCurrency("EUR")
                    .build())
                .setAgent(Agent.newBuilder()
                    .setFinancialInstitutionIdentification("FII")
                    .build())
                .build())
            .setExchangeRate("1.00")
            .setRemittenceInformation("remittence")
            .setInstructedAmount(Amount.newBuilder()
                .setBaseValue("10000")
                .setBaseCurrency("EUR")
                .setCurrency("EUR")
                .setValue("10000")
                .build())
            .setReference("").build())
        .setPartyKey("partyKey")
        .setBalanceBefore((new Amount("cny", "100", "czk", "100")))
        .setCreditor(new AccountDetails("creACN", "creACN", "creBID", "cnn", new Address("test"), "creACN"))
        .setDebtor(new AccountDetails("debBID", "debACN", "debBID", "cnn", new Address("test"), "rf"))
        .build();

    fpsInboundPaymentFraudCheckProducer
        .send(new ProducerRecord<>(requestTopic, null, payload));

    KafkaTestUtils.getSingleRecord(fpsInboundPaymentFraudCheckConsumer, requestTopic);

    waitAtMost(2, TimeUnit.SECONDS).alias(this.requestTopic).untilAsserted(this::isExecutionCompleted);

    FPSInboundPaymentFraudCheck receivedEvent = fpsInboundPaymentArgumentCaptor.getValue();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getSchema(), receivedEvent.getSchema());
    assertEquals(payload.getTransaction().getAmount(), receivedEvent.getTransaction().getAmount());
    assertEquals(payload.getBalanceBefore().getCurrency(), receivedEvent.getBalanceBefore().getCurrency());

    DomesticInPaymentV2 inPaymentV3 = domesticInPaymentV2ArgumentCaptor.getValue();

    assertEquals(payload.getPartyKey(), inPaymentV3.getPartyKey());
    assertEquals(payload.getTransaction().getId(), inPaymentV3.getTransactionId());
    assertEquals(payload.getCreditor().getName(), inPaymentV3.getCreditorName());

  }

  private void isExecutionCompleted()
      throws TransactionMonitoringException, ParseException, DomesticInTransactionMonitoringExceptionV2 {
    Mockito.verify(domesticPaymentEventListener, times(1))
        .handleInboundPaymentEvent(fpsInboundPaymentArgumentCaptor.capture(), any());

    Mockito.verify(transactionMonitoringClient, times(1))
        .checkFinCrimeV2(domesticInPaymentV2ArgumentCaptor.capture());

    assertNotNull(domesticInPaymentV2ArgumentCaptor);
  }
}
