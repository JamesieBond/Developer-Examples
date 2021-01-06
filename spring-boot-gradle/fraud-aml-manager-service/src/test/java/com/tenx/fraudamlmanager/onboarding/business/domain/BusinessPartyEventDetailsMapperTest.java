package com.tenx.fraudamlmanager.onboarding.business.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tenx.fraudamlmanager.onboarding.business.api.BusinessPartyEventMapperService;
import com.tenx.fraudamlmanager.onboarding.business.api.BusinessPartyEventMapperServiceImpl;
import com.tenxbanking.party.event.business.AddressType;
import com.tenxbanking.party.event.business.BusinessAddressStatus;
import com.tenxbanking.party.event.business.BusinessAddressV2;
import com.tenxbanking.party.event.business.BusinessEventV2;
import com.tenxbanking.party.event.business.BusinessType;
import com.tenxbanking.party.event.business.PartyStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Niall O'Connell
 */
public class BusinessPartyEventDetailsMapperTest {

    private String UPDATE_TYPE = "CustomerProvisioned";

    private BusinessPartyEventMapperService businessPartyEventMapperService;

    @BeforeEach
    public void beforeEach() {
        this.businessPartyEventMapperService = new BusinessPartyEventMapperServiceImpl();
    }

    @Test
    public void testBusinessPartyEventMapper() {
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
                        .setStatus(PartyStatus.REGISTERED)
                        .build();

        BusinessPartyDetails businessParty =
                businessPartyEventMapperService.mapBusinessPartyDetails(businessPartyEvent, UPDATE_TYPE);
        assertEquals(businessParty.getPartyKey(), businessPartyEvent.getPartyKey());
        assertEquals(businessParty.getCompanyName(), businessPartyEvent.getFullLegalName());
        assertEquals(
                businessParty.getRegistrationNumber(), businessPartyEvent.getBusinessIdentificationCode());
        assertEquals(
                businessParty.getRegisteredAddress().getAddressLine1(),
                businessPartyEvent.getAddress().get(0).getAddressLine1());
        assertEquals(businessParty.getTradingName(), businessPartyEvent.getTradingName());
        assertEquals(businessParty.getUpdateType(), UPDATE_TYPE);
    }
}
