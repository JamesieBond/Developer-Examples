package com.tenx.fraudamlmanager.cards;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.KafkaTestBase;
import com.tenx.fraudamlmanager.cards.domain.IndividualPartyInfo;
import com.tenx.fraudamlmanager.cards.domain.PartyInfoStoreService;
import com.tenx.fraudamlmanager.cards.infrastructure.PartyInfoEntity;
import com.tenx.fraudamlmanager.cards.infrastructure.PartyInfoEntityRepository;
import com.tenxbanking.individual.event.Address;
import com.tenxbanking.individual.event.AddressType;
import com.tenxbanking.individual.event.IndividualEventV1;
import com.tenxbanking.individual.event.Status;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.jdbc.Sql;

@Sql({"/schema-hsqldb.sql"})
class StoringPostCodeIntegrationTest extends KafkaTestBase {

  @Autowired
  private PartyInfoEntityRepository partyInfoEntityRepository;

  @SpyBean
  private PartyInfoStoreService partyInfoStoreService;

  private Producer<String, IndividualEventV1> individualEventV1Producer;
  private Consumer<String, IndividualEventV1> individualEventV1Consumer;

  @Value("${spring.kafka.consumer.individual-party-v2-topic}")
  private String topic;

  @BeforeEach
  public void initTest() {
    individualEventV1Producer = new KafkaProducer<>(producerProps);

    individualEventV1Consumer = new DefaultKafkaConsumerFactory<String, IndividualEventV1>(
        consumerProps).createConsumer(CONSUMER_GROUP_ID, CLIENT_PREFIX);

    individualEventV1Consumer
        .subscribe(new ArrayList<>(List.of(topic)));

    ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerEndpointRegistry
        .getListenerContainer("IndividualInfoListener");

    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @AfterEach
  public void resetTest() {
    individualEventV1Producer.close();
    individualEventV1Consumer.close();
  }

  @Test
  void testStorePartyInfo() {
    List<Address> addresses = new ArrayList<>();
    addresses.add(Address.newBuilder()
        .setAddressLine1("add1")
        .setPostCode("postCode")
        .setCity("dublin")
        .setCountry("ireland")
        .setStatus(Status.ACTIVE)
        .setAddressType(AddressType.HOME).build());

    IndividualEventV1 individualEventV1 = IndividualEventV1.newBuilder().setPartyKey("partyKey")
        .setStatus(IndividualPartyInfo.STATUS_PROVISIONED)
        .setAddress(addresses)
        .build();

    processKafkaMessage(individualEventV1);
    PartyInfoEntity partyInfoEntity = waitAndRetrievePartyInfoEntity("partyKey");
    assertThat(partyInfoEntity.getPartyKey()).isEqualTo("partyKey");
    assertThat(partyInfoEntity.getPostCode()).isEqualTo("postCode");
  }

  @Test
  void testStorePartyInfoGivenStatusIsNotActive() {
    List<Address> addresses = new ArrayList<>();
    addresses.add(Address.newBuilder()
        .setAddressLine1("add1")
        .setPostCode("postCode")
        .setCity("dublin")
        .setCountry("ireland")
        .setStatus(Status.ACTIVE)
        .setAddressType(AddressType.HOME).build());

    IndividualEventV1 individualEventV1 = IndividualEventV1.newBuilder().setPartyKey("partyKey1")
        .setStatus("TEST")
        .setAddress(addresses)
        .build();

    processKafkaMessage(individualEventV1);
    verifyStoreServiceHasNoInteraction();
  }

  @Test
  void testStorePartyInfoGivenNoActiveAddress() {
    List<Address> addresses = new ArrayList<>();
    addresses.add(Address.newBuilder()
        .setAddressLine1("add1")
        .setPostCode("postCode")
        .setCity("dublin")
        .setCountry("ireland")
        .setStatus(Status.EXPIRED)
        .setAddressType(AddressType.HOME).build());

    IndividualEventV1 individualEventV1 = IndividualEventV1.newBuilder().setPartyKey("partyKey2")
        .setStatus(IndividualPartyInfo.STATUS_PROVISIONED)
        .setAddress(addresses)
        .build();

    processKafkaMessage(individualEventV1);
    verifyStoreServiceHasNoInteraction();
  }

  @Test
  void testStorePartyInfoGivenNoAddress() {
    IndividualEventV1 individualEventV1 = IndividualEventV1.newBuilder().setPartyKey("partyKey3")
        .build();

    processKafkaMessage(individualEventV1);
    verifyStoreServiceHasNoInteraction();
  }

  @Test
  void testStorePartyInfoGivenMultipleActiveAddress() {
    List<Address> addresses = new ArrayList<>();
    addresses.add(Address.newBuilder()
        .setAddressLine1("add1")
        .setPostCode("postCode1")
        .setCity("dublin")
        .setCountry("ireland")
        .setStatus(Status.ACTIVE)
        .setAddressType(AddressType.HOME).build());

    addresses.add(Address.newBuilder()
        .setAddressLine1("add2")
        .setPostCode("postCode2")
        .setCity("dublin")
        .setCountry("ireland")
        .setStatus(Status.ACTIVE)
        .setAddressType(AddressType.HOME).build());

    IndividualEventV1 individualEventV1 = IndividualEventV1.newBuilder().setPartyKey("partyKey4")
        .setStatus(IndividualPartyInfo.STATUS_PROVISIONED)
        .setAddress(addresses)
        .build();

    processKafkaMessage(individualEventV1);
    PartyInfoEntity partyInfoEntity = waitAndRetrievePartyInfoEntity("partyKey4");
    assertThat(partyInfoEntity.getPostCode()).isEqualTo("postCode1");

  }

  @Test
  void testStorePartyInfoGivenMultipleAddressAndSecondElementStatusIsActive() {
    List<Address> addresses = new ArrayList<>();
    addresses.add(Address.newBuilder()
        .setAddressLine1("add1")
        .setPostCode("postCode1")
        .setCity("dublin")
        .setCountry("ireland")
        .setStatus(Status.EXPIRED)
        .setAddressType(AddressType.HOME).build());

    addresses.add(Address.newBuilder()
        .setAddressLine1("add2")
        .setPostCode("postCode2")
        .setCity("dublin")
        .setCountry("ireland")
        .setStatus(Status.ACTIVE)
        .setAddressType(AddressType.HOME).build());

    addresses.add(Address.newBuilder()
        .setAddressLine1("add3")
        .setPostCode("postCode3")
        .setCity("dublin")
        .setCountry("ireland")
        .setStatus(Status.PENDING_VERIFICATION)
        .setAddressType(AddressType.HOME).build());

    IndividualEventV1 individualEventV1 = IndividualEventV1.newBuilder().setPartyKey("partyKey5")
        .setStatus(IndividualPartyInfo.STATUS_PROVISIONED)
        .setAddress(addresses)
        .build();

    processKafkaMessage(individualEventV1);
    PartyInfoEntity partyInfoEntity = waitAndRetrievePartyInfoEntity("partyKey5");
    assertThat(partyInfoEntity.getPostCode()).isEqualTo("postCode2");

  }

  private void processKafkaMessage(IndividualEventV1 individualEventV1) {
    individualEventV1Producer.send(new ProducerRecord<>(topic, null, individualEventV1));
    KafkaTestUtils.getSingleRecord(individualEventV1Consumer, topic);
  }

  private PartyInfoEntity waitAndRetrievePartyInfoEntity(String partyKey) {
    await().alias(this.topic).until(() -> partyInfoEntityRepository.findByPartyKey(partyKey) != null);
    return partyInfoEntityRepository.findByPartyKey(partyKey);
  }

  private void verifyStoreServiceHasNoInteraction() {
    verify(partyInfoStoreService, times(0)).storePartyInfo(any(), any());
  }
}
