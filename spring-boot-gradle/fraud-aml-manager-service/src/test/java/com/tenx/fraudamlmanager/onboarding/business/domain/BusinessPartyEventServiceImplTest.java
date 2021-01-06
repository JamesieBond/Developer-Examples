package com.tenx.fraudamlmanager.onboarding.business.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.business.AddressType;
import com.tenxbanking.party.event.business.BusinessAddressStatus;
import com.tenxbanking.party.event.business.BusinessAddressV2;
import com.tenxbanking.party.event.business.BusinessEventV2;
import com.tenxbanking.party.event.business.BusinessType;
import com.tenxbanking.party.event.business.PartyStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Niall O'Connell
 */
@ExtendWith(SpringExtension.class)
class BusinessPartyEventServiceImplTest {

  @MockBean
  private BusinessPartyEventMetrics businessPartyEventMetrics;

  @MockBean
  TransactionMonitoringClient transactionMonitoringClient;

  private BusinessPartyEventServiceImpl businessPartyEventServiceImpl;

  @BeforeEach
  public void beforeEach() {
    this.businessPartyEventServiceImpl =
        new BusinessPartyEventServiceImpl(businessPartyEventMetrics, transactionMonitoringClient);
  }

  /**
   * @throws Exception Generic exception
   */
  @Test
  void checkWorkingBusinessPartyEventServiceImpl() throws TransactionMonitoringException {

    BusinessEventV2 businessEvent = createBusinessEvent();
    BusinessPartyDetails businessPartyDetails = createBusinessPartyDetails();

    businessEvent.getAddress().get(0).setStatus(BusinessAddressStatus.ACTIVE);
    businessEvent.getAddress().get(0).setAddressType(AddressType.REGD);
    businessPartyDetails.setStatus(PartyStatus.PROVISIONED.name());
    businessPartyDetails.setUpdateType("CustomerModified");
//        given(businessPartyEventMapperService.mapBusinessPartyDetails(any(), anyString()))
//           .willReturn(businessPartyDetails);
    doNothing().when(transactionMonitoringClient).sendBusinessPartyEvent(businessPartyDetails);
    businessPartyEventServiceImpl.processBusinessPartyEvent(businessPartyDetails);
//        Mockito.verify(businessPartyEventMapperService, times(1))
//            .mapBusinessPartyDetails(any(), anyString());
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendBusinessPartyEvent(businessPartyDetails);
    Mockito.verify(businessPartyEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMBusinessPartyRequestsToTMASuccess();
  }

  @Test
  void checkWorkingBusinessPartyEventServiceImplFailed() throws TransactionMonitoringException {

    BusinessEventV2 businessEvent = createBusinessEvent();
    BusinessPartyDetails businessPartyDetails = createBusinessPartyDetails();

    businessEvent.getAddress().get(0).setStatus(BusinessAddressStatus.ACTIVE);
    businessEvent.getAddress().get(0).setAddressType(AddressType.REGD);
    businessPartyDetails.setStatus(PartyStatus.PROVISIONED.name());
    businessPartyDetails.setUpdateType("CustomerModified");
    doThrow(TransactionMonitoringException.class).when(
        transactionMonitoringClient).sendBusinessPartyEvent(any(BusinessPartyDetails.class));
    assertThrows(
        TransactionMonitoringException.class, ()->
            businessPartyEventServiceImpl.processBusinessPartyEvent(businessPartyDetails));
    verify(businessPartyEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMBusinessPartyRequestsToTMAFailed();
  }

  @Test
  void checkNonWorkingBusinessPartyEventServiceImpl() throws TransactionMonitoringException {

    BusinessPartyDetails businessPartyDetails = createBusinessPartyDetails();
    businessPartyDetails.getRegisteredAddress().setStatus(BusinessAddressStatus.EXPIRED.name());

    BusinessEventV2 businessEvent = createBusinessEvent();

//        given(businessPartyEventMapperService.mapBusinessPartyDetails(any(), anyString()))
//            .willReturn(businessPartyDetails);
    doNothing().when(transactionMonitoringClient).sendBusinessPartyEvent(businessPartyDetails);
    businessPartyEventServiceImpl.processBusinessPartyEvent(businessPartyDetails);
//        Mockito.verify(businessPartyEventMapperService, times(1))
//            .mapBusinessPartyDetails(any(), anyString());
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendBusinessPartyEvent(businessPartyDetails);
    Mockito.verify(businessPartyEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMBusinessPartyRequestsToTMASuccess();
  }

  @Test
  void checkNonWorkingBusinessPartyEventServiceImplFailure() throws TransactionMonitoringException {

    BusinessPartyDetails businessPartyDetails = createBusinessPartyDetails();
    businessPartyDetails.getRegisteredAddress().setStatus(BusinessAddressStatus.EXPIRED.name());

    BusinessEventV2 businessEvent = createBusinessEvent();

    businessEvent.getAddress().get(0).setStatus(BusinessAddressStatus.ACTIVE);
    businessEvent.getAddress().get(0).setAddressType(AddressType.REGD);
    businessPartyDetails.setStatus(PartyStatus.PROVISIONED.name());
    businessPartyDetails.setUpdateType("CustomerModified");
    doThrow(TransactionMonitoringException.class).when(
        transactionMonitoringClient).sendBusinessPartyEvent(any(BusinessPartyDetails.class));
    assertThrows(
        TransactionMonitoringException.class, ()->
            businessPartyEventServiceImpl.processBusinessPartyEvent(businessPartyDetails));
    verify(businessPartyEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMBusinessPartyRequestsToTMAFailed();
  }

  private BusinessPartyDetails createBusinessPartyDetails() {
    BusinessPartyDetails businessPartyDetails = new BusinessPartyDetails();
    businessPartyDetails.setCompanyName("fullLegalName");
    businessPartyDetails.setPartyKey("partyKey");
    businessPartyDetails.setRegistrationNumber("businessIdentificationCode");
    businessPartyDetails.setTradingName("TradingName");
    businessPartyDetails.setUpdateType("CustomerProvisioned");
    businessPartyDetails.setStatus(PartyStatus.PROVISIONED.name());

    Address a = new Address();
    a.setAddressLine1("add1");
    a.setPostCode("post");
    a.setCity("city");
    a.setCountry("country");
    a.setState(null);
    a.setStatus("ACTIVE");
    a.setAddressType("REGD");
    businessPartyDetails.setAddress(Arrays.asList(a));

    return businessPartyDetails;
  }

  private BusinessEventV2 createBusinessEvent() {

    BusinessAddressV2 address =
        BusinessAddressV2.newBuilder()
            .setAddressLine1("123")
            .setAddressLine2("Test Street")
            .setAddressLine3("Test Street")
            .setAddressLine4("Test Street")
            .setAddressLine5("Test Street")
            .setCity("Dublin")
            .setCountry("Ireland")
            .setPostCode("D08")
            .setState("Test State")
            .setAddressType(AddressType.REGD)
            .setStatus(BusinessAddressStatus.ACTIVE)
            .build();
    List<BusinessAddressV2> businessAddressesV2 = new ArrayList<>();
    businessAddressesV2.add(address);

    BusinessEventV2 businessEvent =
        BusinessEventV2.newBuilder()
            .setPartyKey("10000")
            .setFullLegalName("124513124")
            .setBusinessIdentificationCode("Blair")
            .setAddress(businessAddressesV2)
            .setTradingName("Deloitte")
            .setTenantKey("tenant")
            .setBusinessType(BusinessType.Charity)
            .setStatus(PartyStatus.REGISTERED)
            .build();
    return businessEvent;
  }
}
