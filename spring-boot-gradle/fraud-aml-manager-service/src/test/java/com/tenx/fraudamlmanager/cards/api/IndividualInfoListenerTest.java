package com.tenx.fraudamlmanager.cards.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cards.domain.IndividualPartyInfo;
import com.tenx.fraudamlmanager.cards.domain.IndividualPartyInfoService;
import com.tenxbanking.individual.event.Address;
import com.tenxbanking.individual.event.AddressType;
import com.tenxbanking.individual.event.IndividualEventV1;
import com.tenxbanking.individual.event.Status;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class IndividualInfoListenerTest {

  @MockBean
  private IndividualPartyInfoService individualPartyInfoService;

  @Mock
  private Acknowledgment acknowledgment;

  private IndividualInfoListener individualInfoListener;

  @BeforeEach
  public void initTest() {
    individualInfoListener = new IndividualInfoListener(individualPartyInfoService);
  }

  @Test
  public void testProcessIndividualPartyEvent() {
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

    ConsumerRecord<String, IndividualEventV1> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", individualEventV1);

    individualInfoListener.processIndividualPartyEvent(consumerRecord, acknowledgment);
    IndividualPartyInfo individualPartyInfo = IndividualEventToIndividualPartyInfoMapper.MAPPER
        .toIndividualPartyInfo(individualEventV1);
    Mockito.verify(individualPartyInfoService, times(1)).storePartyInfo(individualPartyInfo);
    Mockito.verify(acknowledgment, times(1)).acknowledge();
  }

  @Test
  public void testProcessIndividualPartyEventGivenExceptionIsThrown() {
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

    ConsumerRecord<String, IndividualEventV1> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", individualEventV1);

    doThrow(new NullPointerException("test")).when(individualPartyInfoService)
        .storePartyInfo(any());
    assertThrows(
        NullPointerException.class, () ->
            individualInfoListener.processIndividualPartyEvent(consumerRecord, acknowledgment));
    IndividualPartyInfo individualPartyInfo = IndividualEventToIndividualPartyInfoMapper.MAPPER
        .toIndividualPartyInfo(individualEventV1);
    Mockito.verify(individualPartyInfoService, times(1)).storePartyInfo(individualPartyInfo);
    Mockito.verify(acknowledgment, times(0)).acknowledge();
  }

}
