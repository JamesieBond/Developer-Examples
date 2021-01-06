package com.tenx.fraudamlmanager.onboarding.business;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.onboarding.business.api.BusinessPartyEventListener;
import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.business.AddressType;
import com.tenxbanking.party.event.business.BusinessAddressStatus;
import com.tenxbanking.party.event.business.BusinessAddressV2;
import com.tenxbanking.party.event.business.BusinessEventV2;
import com.tenxbanking.party.event.business.BusinessType;
import com.tenxbanking.party.event.business.PartyStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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


class BusinessIntegrationTest extends KafkaTestBase {

  @MockBean
  private TransactionMonitoringClient transactionMonitoringClient;

  @SpyBean
  private BusinessPartyEventListener businessPartyEventListener;

  @Captor
  private ArgumentCaptor<ConsumerRecord<String, BusinessEventV2>> consumerRecordArgumentCaptor;

  @Captor
  private ArgumentCaptor<BusinessPartyDetails> businessPartyDetailsArgumentCaptor;

  private Producer<String, BusinessEventV2> businessEventProducer;
  private Consumer<String, BusinessEventV2> businessEventConsumer;

  @Value("${spring.kafka.consumer.party-event-business-v2-topic}")
  private String topic;

  @Override
  @BeforeEach
  public void initTest() {
    businessEventProducer = new KafkaProducer<>(producerProps);

    businessEventConsumer = new DefaultKafkaConsumerFactory<String, BusinessEventV2>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    businessEventConsumer
        .subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("BusinessPartyEventListener");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @Override
  @AfterEach
  public void resetTest() {
    businessEventProducer.close();
    businessEventConsumer.close();
  }

  private static Stream<Arguments> payloadsHappyPath() {

    List<BusinessAddressV2> businessAddress = new ArrayList<>();
    businessAddress.add(
        BusinessAddressV2.newBuilder()
            .setAddressLine1("add1")
            .setPostCode("post")
            .setCity("city")
            .setCountry("country")
            .setStatus(BusinessAddressStatus.ACTIVE)
            .setAddressType(AddressType.REGD)
            .build());

    BusinessEventV2 businessPartyEvent =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddress)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.PROVISIONED)
            .build();

    List<Header> customerProvisionedHeader = new ArrayList<>();
    customerProvisionedHeader.add(new RecordHeader("EventType", "CustomerProvisioned".getBytes()));

    List<Header> customerModifiedHeaders = new ArrayList<>();
    customerModifiedHeaders.add(new RecordHeader("EventType", "CustomerModified".getBytes()));

    return Stream.of(
        Arguments.of(businessPartyEvent, customerModifiedHeaders, "CustomerModified"),
        Arguments.of(businessPartyEvent, customerProvisionedHeader, "CustomerProvisioned")
    );
  }

  private static Stream<Arguments> NoAddressSentPayloads() {

    List<BusinessAddressV2> businessAddressStatusPending = new ArrayList<>();
    businessAddressStatusPending.add(
        BusinessAddressV2.newBuilder()
            .setAddressLine1("add1")
            .setPostCode("post")
            .setCity("city")
            .setCountry("country")
            .setStatus(BusinessAddressStatus.PENDING_VERIFICATION)
            .setAddressType(AddressType.REGD)
            .build());

    BusinessEventV2 businessPartyEventAddressStatusPending =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddressStatusPending)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.PROVISIONED)
            .build();

    List<BusinessAddressV2> businessAddressStatusExpired = new ArrayList<>();
    businessAddressStatusExpired.add(
        BusinessAddressV2.newBuilder()
            .setAddressLine1("add1")
            .setPostCode("post")
            .setCity("city")
            .setCountry("country")
            .setStatus(BusinessAddressStatus.EXPIRED)
            .setAddressType(AddressType.REGD)
            .build());

    BusinessEventV2 businessPartyEventAddressStatusExpired =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddressStatusExpired)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.PROVISIONED)
            .build();

    List<BusinessAddressV2> businessAddressStatusAddressTypeHome = new ArrayList<>();
    businessAddressStatusAddressTypeHome.add(
        BusinessAddressV2.newBuilder()
            .setAddressLine1("add1")
            .setPostCode("post")
            .setCity("city")
            .setCountry("country")
            .setStatus(BusinessAddressStatus.ACTIVE)
            .setAddressType(AddressType.HOME)
            .build());

    BusinessEventV2 businessPartyEventAddressTypeHome =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddressStatusAddressTypeHome)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.PROVISIONED)
            .build();

    List<BusinessAddressV2> businessAddressStatusAddressTypeBizz = new ArrayList<>();
    businessAddressStatusAddressTypeBizz.add(
        BusinessAddressV2.newBuilder()
            .setAddressLine1("add1")
            .setPostCode("post")
            .setCity("city")
            .setCountry("country")
            .setStatus(BusinessAddressStatus.ACTIVE)
            .setAddressType(AddressType.BIZZ)
            .build());

    BusinessEventV2 businessPartyEventAddressTypeBizz =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddressStatusAddressTypeBizz)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.PROVISIONED)
            .build();

    List<Header> customerProvisionedHeader = new ArrayList<>();
    customerProvisionedHeader.add(new RecordHeader("EventType", "CustomerProvisioned".getBytes()));

    return Stream.of(
        Arguments.of(businessPartyEventAddressStatusPending, customerProvisionedHeader, "CustomerProvisioned"),
        Arguments.of(businessPartyEventAddressStatusExpired, customerProvisionedHeader, "CustomerProvisioned"),
        Arguments.of(businessPartyEventAddressTypeHome, customerProvisionedHeader, "CustomerProvisioned"),
        Arguments.of(businessPartyEventAddressTypeBizz, customerProvisionedHeader, "CustomerProvisioned")
    );
  }

  private static Stream<Arguments> TMANotCalledPayloads() {

    List<BusinessAddressV2> businessAddress = new ArrayList<>();
    businessAddress.add(
        BusinessAddressV2.newBuilder()
            .setAddressLine1("add1")
            .setPostCode("post")
            .setCity("city")
            .setCountry("country")
            .setStatus(BusinessAddressStatus.ACTIVE)
            .setAddressType(AddressType.BIZZ)
            .build());

    BusinessEventV2 businessPartyEventPArtyStatusInactive =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddress)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.INACTIVE)
            .build();

    BusinessEventV2 businessPartyEventPartyStatusProspect =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddress)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.PROSPECT)
            .build();

    BusinessEventV2 businessPartyEventPartyStatusRegistered =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddress)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.REGISTERED)
            .build();

    BusinessEventV2 businessPartyEventPartyStatusRejected =
        BusinessEventV2.newBuilder()
            .setPartyKey("partyKey")
            .setFullLegalName("fullLegalName")
            .setAddress(businessAddress)
            .setBusinessIdentificationCode("businessIdentificationCode")
            .setTradingName("TradingName")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.REJECTED)
            .build();

    List<Header> customerModifiedHeaders = new ArrayList<>();
    customerModifiedHeaders.add(new RecordHeader("EventType", "CustomerModified".getBytes()));

    return Stream.of(
        Arguments.of(businessPartyEventPArtyStatusInactive, customerModifiedHeaders),
        Arguments.of(businessPartyEventPartyStatusProspect, customerModifiedHeaders),
        Arguments.of(businessPartyEventPartyStatusRegistered, customerModifiedHeaders),
        Arguments.of(businessPartyEventPartyStatusRejected, customerModifiedHeaders)
    );
  }

  @ParameterizedTest
  @MethodSource("payloadsHappyPath")
  void businessPartyE2ETestHappyPath(BusinessEventV2 payload, List<Header> header, String eventType)
      throws TransactionMonitoringException {

    processKafkaMessageAndCallTMA(payload, header);

    Mockito.verify(businessPartyEventListener, times(1))
        .handleBusinessPartyEvent(consumerRecordArgumentCaptor.capture(), any(), any());

    BusinessEventV2 receivedEvent = consumerRecordArgumentCaptor.getValue().value();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getFullLegalName(), receivedEvent.getFullLegalName());
    assertEquals(payload.getAddress().get(0).getAddressLine1(),
        receivedEvent.getAddress().get(0).getAddressLine1());
    assertEquals(payload.getTradingName(), receivedEvent.getTradingName());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendBusinessPartyEvent(businessPartyDetailsArgumentCaptor.capture());

    BusinessPartyDetails capturedDetails = businessPartyDetailsArgumentCaptor.getValue();

    assertEquals(payload.getPartyKey(), capturedDetails.getPartyKey());
    assertEquals(payload.getFullLegalName(), capturedDetails.getCompanyName());
    assertEquals(payload.getBusinessIdentificationCode(),
        capturedDetails.getRegistrationNumber());
    assertEquals(payload.getAddress().get(0).getAddressLine1(),
        capturedDetails.getRegisteredAddress().getAddressLine1());
    assertEquals(payload.getTradingName(), capturedDetails.getTradingName());
    assertEquals(eventType, capturedDetails.getUpdateType());
  }

  @ParameterizedTest
  @MethodSource("TMANotCalledPayloads")
  void businessPartyE2ETMANotCalled(BusinessEventV2 payload, List<Header> header)
      throws TransactionMonitoringException {

    processKafkaMessageAndDontCallTMA(payload, header);

    Mockito.verify(businessPartyEventListener, times(1))
        .handleBusinessPartyEvent(consumerRecordArgumentCaptor.capture(), any(), any());

    BusinessEventV2 receivedEvent = consumerRecordArgumentCaptor.getValue().value();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getFullLegalName(), receivedEvent.getFullLegalName());
    assertEquals(payload.getTradingName(), receivedEvent.getTradingName());

    Mockito.verify(transactionMonitoringClient, times(0))
        .sendBusinessPartyEvent(businessPartyDetailsArgumentCaptor.capture());
  }

  @ParameterizedTest
  @MethodSource("NoAddressSentPayloads")
  void businessPartyE2ENoAddressSent(BusinessEventV2 payload, List<Header> header, String eventType)
      throws TransactionMonitoringException {

    processKafkaMessageAndCallTMA(payload, header);

    Mockito.verify(businessPartyEventListener, times(1))
        .handleBusinessPartyEvent(consumerRecordArgumentCaptor.capture(), any(), any());

    BusinessEventV2 receivedEvent = consumerRecordArgumentCaptor.getValue().value();
    assertEquals(payload.getPartyKey(), receivedEvent.getPartyKey());
    assertEquals(payload.getFullLegalName(), receivedEvent.getFullLegalName());
    assertEquals(payload.getAddress().get(0).getAddressLine1(),
        receivedEvent.getAddress().get(0).getAddressLine1());
    assertEquals(payload.getTradingName(), receivedEvent.getTradingName());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendBusinessPartyEvent(businessPartyDetailsArgumentCaptor.capture());

    BusinessPartyDetails capturedDetails = businessPartyDetailsArgumentCaptor.getValue();

    assertEquals(payload.getPartyKey(), capturedDetails.getPartyKey());
    assertEquals(payload.getFullLegalName(), capturedDetails.getCompanyName());
    assertEquals(payload.getBusinessIdentificationCode(),
        capturedDetails.getRegistrationNumber());
    assertNull(capturedDetails.getRegisteredAddress());
    assertEquals(payload.getTradingName(), capturedDetails.getTradingName());
    assertEquals(eventType, capturedDetails.getUpdateType());
  }

  private void processKafkaMessageAndCallTMA(BusinessEventV2 businessEventV2, List<Header> header) {
    businessEventProducer
        .send(new ProducerRecord<>(topic, null, null, null, businessEventV2, header));
    KafkaTestUtils.getSingleRecord(businessEventConsumer, topic);
    waitAtMost(1, TimeUnit.SECONDS).alias(this.topic).untilAsserted(this::isExecutionCompletedAndTMAisCalled);
  }

  private void isExecutionCompletedAndTMAisCalled() throws TransactionMonitoringException {
    Mockito.verify(businessPartyEventListener, times(1))
        .handleBusinessPartyEvent(consumerRecordArgumentCaptor.capture(), any(), any());

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendBusinessPartyEvent(businessPartyDetailsArgumentCaptor.capture());

    assertNotNull(consumerRecordArgumentCaptor.getValue());
    assertNotNull(businessPartyDetailsArgumentCaptor.getValue());
  }

  private void processKafkaMessageAndDontCallTMA(BusinessEventV2 businessEventV2, List<Header> header) {
    businessEventProducer
        .send(new ProducerRecord<>(topic, null, null, null, businessEventV2, header));
    KafkaTestUtils.getSingleRecord(businessEventConsumer, topic);
    waitAtMost(2, TimeUnit.SECONDS).alias(this.topic).untilAsserted(this::isExecutionCompletedAndTMAisNotCalled);
  }

  private void isExecutionCompletedAndTMAisNotCalled() throws TransactionMonitoringException {
    Mockito.verify(businessPartyEventListener, times(1))
        .handleBusinessPartyEvent(consumerRecordArgumentCaptor.capture(), any(), any());
    assertNotNull(consumerRecordArgumentCaptor.getValue());
  }

}
