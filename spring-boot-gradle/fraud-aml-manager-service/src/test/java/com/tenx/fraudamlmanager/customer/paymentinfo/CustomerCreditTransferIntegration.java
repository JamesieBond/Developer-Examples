package com.tenx.fraudamlmanager.customer.paymentinfo;

import static java.time.Instant.ofEpochMilli;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.atLeast;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.customer.paymentinfo.api.CustomerCreditTransferEventListener;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.AcceptedCustomerCreditTransferRequest;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferException;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferMetrics;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.RejectedCustomerCreditTranferRequest;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.types.Account;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.types.Agent;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.types.CurrencyAndAmount;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.types.User;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenx.fraudamlmanager.infrastructure.feedzaimanager.FeedzaiManagerClient;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;


public class CustomerCreditTransferIntegration extends KafkaTestBase {

  @SpyBean
  CustomerCreditTransferEventListener customerCreditTransferEventListener;
  @MockBean
  PaymentsDeviceProfileService paymentsDeviceProfileService;
  @MockBean
  FeedzaiManagerClient feedzaiManagerClient;
  @MockBean
  CustomerCreditTransferMetrics customerCreditTransferMetrics;
  CustomerCreditTransferInitiationCompletedEvent cctiCompletedEvent;
  private Producer<String, CustomerCreditTransferInitiationCompletedEvent> cctiEventProducer;
  private Consumer<String, CustomerCreditTransferInitiationCompletedEvent> cctiEventConsumer;
  @Value("${spring.kafka.consumer.payments-cct-initiation-completed-topic}")
  private String topic;
  @Captor
  private ArgumentCaptor<ConsumerRecord<String, CustomerCreditTransferInitiationCompletedEvent>> crArgumentCaptor;
  @Captor
  private ArgumentCaptor<AcceptedCustomerCreditTransferRequest> acceptedCustomerCreditTransferRequestArgumentCaptor;
  @Captor
  private ArgumentCaptor<RejectedCustomerCreditTranferRequest> rejectedCustomerCreditTranferRequestArgumentCaptor;

  @Test
  @DirtiesContext
  void testRejectedGroupStatus() throws IOException {
    cctiCompletedEvent = CustomerCreditTransferHelper.readEventFromFile();

    PaymentDeviceProfile paymentsDeviceProfile = new PaymentDeviceProfile();
    paymentsDeviceProfile.setDeviceKeyId("keyID");

    BDDMockito.given(
        paymentsDeviceProfileService.fetchThreatMetrixResultUsingPartyKey("13tr8028-9825-4341-8600-4a2e159ff43b"))
        .willReturn(paymentsDeviceProfile);

    processKafkaMessage(cctiCompletedEvent);

    Mockito.verify(feedzaiManagerClient, times(1))
        .checkCustomerCreditTransferStatus(rejectedCustomerCreditTranferRequestArgumentCaptor.capture());
    Mockito.verify(customerCreditTransferMetrics, times(1)).incrementRejectedTotalCounter();
    Mockito.verify(customerCreditTransferMetrics, times(0)).incrementRejectedFailedCounter();

    RejectedCustomerCreditTranferRequest rejectedCustomerCreditTranferRequest = rejectedCustomerCreditTranferRequestArgumentCaptor
        .getValue();

    assertEquals("2d94fc28-1d2b-4430-b2ce-0a5db9a3484a",
        rejectedCustomerCreditTranferRequest.getTransactionTraceIdentification());
    assertEquals("RJCT", rejectedCustomerCreditTranferRequest.getGroupStatus());
    assertEquals("internal_transfer", rejectedCustomerCreditTranferRequest.getRoutingDestination());
    assertEquals("notHonor", rejectedCustomerCreditTranferRequest.getInfoType());

    assertEquals(LocalDateTime
            .ofInstant(ofEpochMilli(1604310181820L), TimeZone.getDefault().toZoneId()),
        rejectedCustomerCreditTranferRequest.getCreationDateTime());

    assertEquals("13tr8028-9825-4341-8600-4a2e159ff43b", rejectedCustomerCreditTranferRequest.getPartyKey());

    assertEquals(new CurrencyAndAmount(new BigDecimal("1.00"), "AUD"),
        rejectedCustomerCreditTranferRequest.getInstructedAmount());
    assertEquals(new CurrencyAndAmount(BigDecimal.valueOf(0.97), "EUR"),
        rejectedCustomerCreditTranferRequest.getSettlementAmount());

    User debtor = new User(new Agent("037886"), new Account("c5b520dd38464ccf94000f4e32c27fc9"));

    assertEquals(debtor, rejectedCustomerCreditTranferRequest.getDebtor());

    User creditor = new User(new Agent("040016"), new Account("040016497922050"));

    assertEquals(creditor, rejectedCustomerCreditTranferRequest.getCreditor());
    assertEquals("testPartyName", rejectedCustomerCreditTranferRequest.getCreditorName());

  }

  @Test
  @DirtiesContext
  void testAcceptedGroupStatus() throws IOException {
    cctiCompletedEvent = CustomerCreditTransferHelper.readEventFromFile();
    cctiCompletedEvent.getCustomerPaymentStatusReport().getOriginalGroupInformationAndStatus()
        .setGroupStatus("ACSC");

    PaymentDeviceProfile paymentsDeviceProfile = new PaymentDeviceProfile();
    paymentsDeviceProfile.setDeviceKeyId("keyID");

    BDDMockito.given(
        paymentsDeviceProfileService.fetchThreatMetrixResultUsingPartyKey("13tr8028-9825-4341-8600-4a2e159ff43b"))
        .willReturn(paymentsDeviceProfile);

    processKafkaMessage(cctiCompletedEvent);

    Mockito.verify(feedzaiManagerClient, times(1))
        .checkCustomerCreditTransferStatus(acceptedCustomerCreditTransferRequestArgumentCaptor.capture());

    Mockito.verify(customerCreditTransferMetrics, times(1)).incrementAcceptedTotalCounter();
    Mockito.verify(customerCreditTransferMetrics, times(0)).incrementAcceptedFailedCounter();

    AcceptedCustomerCreditTransferRequest acceptedCustomerCreditTransferRequest = acceptedCustomerCreditTransferRequestArgumentCaptor
        .getValue();

    assertEquals("ACSC", acceptedCustomerCreditTransferRequest.getGroupStatus());
    assertEquals("2d94fc28-1d2b-4430-b2ce-0a5db9a3484a",
        acceptedCustomerCreditTransferRequest.getTransactionTraceIdentification());
    assertEquals(Integer.valueOf(2), acceptedCustomerCreditTransferRequest.getNumberOfTransactions());
    assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(1604310181820L),
        TimeZone.getDefault().toZoneId()),
        acceptedCustomerCreditTransferRequest.getCreationDateTime());
    assertEquals("keyID", acceptedCustomerCreditTransferRequest.getDeviceId());
    assertEquals(LocalDate.of(2020, 9, 29), acceptedCustomerCreditTransferRequest.getSettlementDate());
    assertFalse(acceptedCustomerCreditTransferRequest.isSenderUrgency());
    assertFalse(acceptedCustomerCreditTransferRequest.isRecurringPayment());
    assertEquals("telephone", acceptedCustomerCreditTransferRequest.getChannelType());

  }


  @BeforeEach
  @Override
  public void initTest() {

    cctiEventProducer = new KafkaProducer<>(producerProps);
    cctiEventConsumer = new DefaultKafkaConsumerFactory<String, CustomerCreditTransferInitiationCompletedEvent>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID,
        CLIENT_PREFIX);
    cctiEventConsumer.subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("CustomerCreditTransferInitiationCompletedEventListener");
    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @Override
  public void resetTest() {
    cctiEventConsumer.close();
    cctiEventProducer.close();
  }

  private void processKafkaMessage(CustomerCreditTransferInitiationCompletedEvent event) {
    cctiEventProducer.send(new ProducerRecord<>(topic, "0", event));
    KafkaTestUtils.getSingleRecord(cctiEventConsumer, topic);
    waitForKafkaListenerToProcess();

    waitAtMost(1, TimeUnit.SECONDS).alias(this.topic).untilAsserted(this::isExecutionCompleted);


  }

  private void isExecutionCompleted() throws CustomerCreditTransferException {
    Mockito.verify(customerCreditTransferEventListener, times(1))
        .handleCustomerCreditTransferInitiationCompletedEvent(crArgumentCaptor.capture(), any());

    Mockito.verify(feedzaiManagerClient, atLeast(0))
        .checkCustomerCreditTransferStatus(rejectedCustomerCreditTranferRequestArgumentCaptor.capture());

    Mockito.verify(feedzaiManagerClient, atLeast(0))
        .checkCustomerCreditTransferStatus(acceptedCustomerCreditTransferRequestArgumentCaptor.capture());

    assertNotNull(crArgumentCaptor.getValue());
  }

  private void waitForKafkaListenerToProcess() {
    waitAtMost(1, TimeUnit.SECONDS).alias(this.topic).untilAsserted(this::isExecutionCompleted);
  }

}
