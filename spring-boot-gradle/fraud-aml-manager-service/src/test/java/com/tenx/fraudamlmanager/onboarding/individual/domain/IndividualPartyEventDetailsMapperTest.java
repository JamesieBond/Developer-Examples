package com.tenx.fraudamlmanager.onboarding.individual.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyDetails;
import com.tenxbanking.party.event.CustomerAddressV3;
import com.tenxbanking.party.event.CustomerEventV3;
import com.tenxbanking.party.event.PartyContactStatusV3;
import com.tenxbanking.party.event.PartyStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IndividualPartyEventDetailsMapperTest {
  private static final String UPDATE_TYPE = "CustomerModified";

  private IndividualPartyEventMapperServiceImpl individualPartyEventMapperServiceImpl;

  @BeforeEach
  public void beforeEach() {
    this.individualPartyEventMapperServiceImpl = new IndividualPartyEventMapperServiceImpl();
  }

  @Test
  public void checkIndividualPartyEventMapperService() {
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

    CustomerEventV3 partyEvent =
        CustomerEventV3.newBuilder()
            .setPartyKey("partyKey")
            .setTenantKey("tenantKey")
            .setMobileNumber("08511133344")
            .setEmail("10x@bank.com")
            .setAddress(addressV3s)
            .setBirthDate(623980800)
            .setStatus(PartyStatus.PROSPECT)
            .build();

    IndividualPartyDetails party = individualPartyEventMapperServiceImpl.mapIndividualPartyDetails(partyEvent, UPDATE_TYPE);
    assertEquals(party.getPartyKey(), partyEvent.getPartyKey());
    assertEquals(party.getMobileNumber(), partyEvent.getMobileNumber());
    assertEquals(party.getEmail(), partyEvent.getEmail());
    assertEquals(party.getBirthDate(), 623980800);
    assertEquals("1989-10-10", party.getDateOfBirth().toString());
    assertEquals(
        party.getCurrentAddress().getAddressLine1(),
        partyEvent.getAddress().get(0).getAddressLine1());
    assertEquals(UPDATE_TYPE, party.getUpdateType());
  }

}
