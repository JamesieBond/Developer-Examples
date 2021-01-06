package com.tenx.fraudamlmanager.cards.domain;

import com.tenx.fraudamlmanager.cards.domain.IndividualAddress.Status;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class IndividualPartyInfoServiceImplTest {

  @MockBean
  PartyInfoStoreService partyInfoStoreService;

  private static IndividualPartyInfoService individualPartyInfoService;

  @BeforeEach
  public void initTest() {
    individualPartyInfoService = new IndividualPartyInfoServiceImpl(partyInfoStoreService);
  }

  @Test
  public void testStorePartyInfo() {
    IndividualPartyInfo partyInfo = new IndividualPartyInfo();
    List<IndividualAddress> individualAddressList = new ArrayList<>();
    individualAddressList.add(new IndividualAddress("postCode", Status.ACTIVE));

    partyInfo.setPartyKey("partyKey");
    partyInfo.setStatus(IndividualPartyInfo.STATUS_PROVISIONED);
    partyInfo.setIndividualAddressList(individualAddressList);

    individualPartyInfoService.storePartyInfo(partyInfo);
    Mockito.verify(partyInfoStoreService, Mockito.times(1)).storePartyInfo("partyKey", "postCode");

  }

  @Test
  public void testStorePartyInfoGivenStatusIsNotActive() {
    IndividualPartyInfo partyInfo = new IndividualPartyInfo();
    List<IndividualAddress> individualAddressList = new ArrayList<>();
    individualAddressList.add(new IndividualAddress("postCode", Status.ACTIVE));

    partyInfo.setPartyKey("partyKey");
    partyInfo.setStatus("TEST");
    partyInfo.setIndividualAddressList(individualAddressList);

    individualPartyInfoService.storePartyInfo(partyInfo);
    Mockito.verify(partyInfoStoreService, Mockito.times(0)).storePartyInfo("partyKey", "postCode");

  }

  @Test
  public void testStorePartyInfoGivenNonActiveAddress() {
    IndividualPartyInfo partyInfo = new IndividualPartyInfo();
    List<IndividualAddress> individualAddressList = new ArrayList<>();
    individualAddressList.add(new IndividualAddress("postCode", Status.EXPIRED));

    partyInfo.setPartyKey("partyKey");
    partyInfo.setStatus(IndividualPartyInfo.STATUS_PROVISIONED);
    partyInfo.setIndividualAddressList(individualAddressList);

    individualPartyInfoService.storePartyInfo(partyInfo);
    Mockito.verify(partyInfoStoreService, Mockito.times(0)).storePartyInfo("partyKey", "postCode");

  }

  @Test
  public void testStorePartyInfoGivenMultipleAddress() {
    IndividualPartyInfo partyInfo = new IndividualPartyInfo();
    List<IndividualAddress> individualAddressList = new ArrayList<>();
    individualAddressList.add(new IndividualAddress("postCode1", Status.ACTIVE));
    individualAddressList.add(new IndividualAddress("postCode2", Status.ACTIVE));

    partyInfo.setPartyKey("partyKey");
    partyInfo.setStatus(IndividualPartyInfo.STATUS_PROVISIONED);
    partyInfo.setIndividualAddressList(individualAddressList);

    individualPartyInfoService.storePartyInfo(partyInfo);
    Mockito.verify(partyInfoStoreService, Mockito.times(1)).storePartyInfo("partyKey", "postCode1");


  }

  @Test
  public void testStorePartyInfoGivenMultipleAddressAndSecondElementStatusIsActive() {
    IndividualPartyInfo partyInfo = new IndividualPartyInfo();
    List<IndividualAddress> individualAddressList = new ArrayList<>();
    individualAddressList.add(new IndividualAddress("postCode1", Status.PENDING_VERIFICATION));
    individualAddressList.add(new IndividualAddress("postCode2", Status.ACTIVE));
    individualAddressList.add(new IndividualAddress("postCode3", Status.EXPIRED));

    partyInfo.setPartyKey("partyKey");
    partyInfo.setStatus(IndividualPartyInfo.STATUS_PROVISIONED);
    partyInfo.setIndividualAddressList(individualAddressList);

    individualPartyInfoService.storePartyInfo(partyInfo);
    Mockito.verify(partyInfoStoreService, Mockito.times(1)).storePartyInfo("partyKey", "postCode2");

  }
}
