package com.tenx.fraudamlmanager.onboarding.individual;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyEventListener;
import com.tenx.fraudamlmanager.onboarding.individual.domain.IndividualPartyEventService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.CustomerAddressV3;
import com.tenxbanking.party.event.CustomerEventV3;
import com.tenxbanking.party.event.PartyContactStatusV3;
import com.tenxbanking.party.event.PartyStatus;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

class IndividualPartyEventIntegrationTest extends KafkaTestBase {

  @SpyBean
  private IndividualPartyEventListener individualPartyEventListener;

  @SpyBean
  private IndividualPartyEventService individualPartyEventService;

  private Producer<String, CustomerEventV3> customerEventV3Producer;
  private Consumer<String, CustomerEventV3> customerEventV3Consumer;

  @Value("${spring.kafka.consumer.party-event-v3-topic}")
  private String topic;

  @BeforeEach
  public void initTest() {
    customerEventV3Producer = new KafkaProducer<>(producerProps);

    customerEventV3Consumer = new DefaultKafkaConsumerFactory<String, CustomerEventV3>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    customerEventV3Consumer
        .subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("IndividualPartyEvent");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @AfterEach
  public void resetTest() {
    customerEventV3Consumer.close();
    customerEventV3Producer.close();
  }

  @Test
  void testCustomerEvent() throws TransactionMonitoringException {

    List<CustomerAddressV3> addressV3s = new ArrayList<>();
    addressV3s.add(
        CustomerAddressV3.newBuilder()
            .setAddressLine1("add1")
            .setPostCode("post")
            .setCity("city")
            .setCountry("country")
            .setStatus(PartyContactStatusV3.ACTIVE)
            .setAddressType("HOME")
            .build());

    CustomerEventV3 customerEvent =
        CustomerEventV3.newBuilder()
            .setPartyKey("partyKey")
            .setTenantKey("tenantKey")
            .setMobileNumber("08511133344")
            .setEmail("10x@bank.com")
            .setAddress(addressV3s)
            .setBirthDate(623980800)
            .setStatus(PartyStatus.PROSPECT)
            .build();

    doThrow(TransactionMonitoringException.class).when(individualPartyEventService)
        .processIndividualPartyEvent(any(), any());

    List<Header> headers = new ArrayList<>();
    headers.add(new RecordHeader("EventType", "Updated".getBytes()));

    customerEventV3Producer.send(new ProducerRecord<>(topic, 0, "123", customerEvent, headers));
    ConsumerRecord<String, CustomerEventV3> consumerRecord = KafkaTestUtils
        .getSingleRecord(customerEventV3Consumer, topic);

    Acknowledgment acknowledgment = Mockito.mock(Acknowledgment.class);

    assertDoesNotThrow(
        () -> individualPartyEventListener.handleIndividualPartyEvent(consumerRecord, acknowledgment, "Update"));

    verifyNoInteractions(acknowledgment);

  }

}
