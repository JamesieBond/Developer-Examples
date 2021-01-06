package com.tenx.fraudamlmanager.onboarding.individual.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.onboarding.individual.api.Address;
import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.CustomerAddressV3;
import com.tenxbanking.party.event.CustomerEventV3;
import com.tenxbanking.party.event.Document;
import com.tenxbanking.party.event.ExternalIdentifier;
import com.tenxbanking.party.event.PartyContactStatusV3;
import com.tenxbanking.party.event.PartyStatus;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author James Spencer
 */
@ExtendWith(SpringExtension.class)
class IndividualPartyEventServiceImplTest {

  @MockBean
  private IndividualPartyEventMetrics individualPartyEventMetrics;

  @MockBean
  TransactionMonitoringClient transactionMonitoringClient;

  @MockBean
  IndividualPartyEventMapperService individualPartyEventMapperService;

  private IndividualPartyEventServiceImpl individualPartyEventServiceImpl;

  @BeforeEach
  public void beforeEach() {
    this.individualPartyEventServiceImpl =
        new IndividualPartyEventServiceImpl(individualPartyEventMetrics,
            transactionMonitoringClient, individualPartyEventMapperService);
  }

  /**
   * @throws Exception Generic exception
   */
  @Test
  void checkIndividualPartyEventServiceCallToTMA() throws TransactionMonitoringException {
    IndividualPartyDetails individualPartyDetails = createIndividualPartyDetails();
    CustomerEventV3 customerEventV3 = createCustomerEventV3();

    given(individualPartyEventMapperService.mapIndividualPartyDetails(any(), anyString()))
        .willReturn(individualPartyDetails);
    doNothing().when(transactionMonitoringClient).sendIndividualPartyEvent(individualPartyDetails);
    individualPartyEventServiceImpl.processIndividualPartyEvent(customerEventV3, "EventModified");

    Mockito.verify(individualPartyEventMapperService, times(1))
        .mapIndividualPartyDetails(any(), anyString());
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendIndividualPartyEvent(individualPartyDetails);
    Mockito.verify(individualPartyEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMIndividualPartyRequestsToTMASuccess();
  }

  @Test
  void checkIndividualÎPartyEventServiceCallToTMAFailure() throws TransactionMonitoringException {
    IndividualPartyDetails individualPartyDetails = createIndividualPartyDetails();
    CustomerEventV3 customerEventV3 = createCustomerEventV3();
    given(individualPartyEventMapperService.mapIndividualPartyDetails(any(), anyString()))
        .willReturn(individualPartyDetails);
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendIndividualPartyEvent(any(IndividualPartyDetails.class));
    assertThrows(
        TransactionMonitoringException.class, ()->
            individualPartyEventServiceImpl.processIndividualPartyEvent(customerEventV3, "EventModified"));
    verify(individualPartyEventMetrics, times(1))
        .incrementFAMIndividualPartyRequestsToTMAFailed();
  }

  private CustomerEventV3 createCustomerEventV3() {
    CustomerAddressV3 customerAddressV3 =
        CustomerAddressV3.newBuilder()
            .setAddressLine1("123")
            .setAddressLine2("Test Street")
            .setCity("Dublin")
            .setCountry("Ireland")
            .setPostCode("D08")
            .setCreatedDate(ZonedDateTime.now().toString())
            .setStatus(PartyContactStatusV3.ACTIVE)
            .setAddressType("HOME")
            .build();

    List<CustomerAddressV3> customerAddresses = new ArrayList<>();
    customerAddresses.add(customerAddressV3);
    ExternalIdentifier externalIdentifier =
        ExternalIdentifier.newBuilder().setExternalIdentifier("Test").build();

    List<ExternalIdentifier> externalIdentifiers = new ArrayList<>();
    externalIdentifiers.add(externalIdentifier);
    Document document = Document.newBuilder().build();
    List<Document> documents = new ArrayList<>();
    documents.add(document);

    CustomerEventV3 customerEventV3 =
        CustomerEventV3.newBuilder()
            .setCreatedDate(ZonedDateTime.now().toString())
            .setUpdatedDate(ZonedDateTime.now().toString())
            .setPartyKey(UUID.randomUUID().toString())
            .setTenantKey("10000")
            .setMobileNumber("124513124")
            .setEmail("test@email.com")
            .setGivenName("Joe")
            .setMiddleName("Blair")
            .setLastName("Bloggs")
            .setBirthDate(623980800)
            .setAddress(customerAddresses)
            .setStatus(PartyStatus.PROVISIONED)
            .setEcis(externalIdentifiers)
            .setDocument(documents)
            .build();

    return customerEventV3;

  }

  private IndividualPartyDetails createIndividualPartyDetails() {
    Address address = new Address();
    address.setStatus("ACTIVE");
    address.setAddressType("HOME");
    address.setAddressLine1("123");
    address.setAddressLine2("Test Street");
    address.setCity("Dublin");
    address.setCountry("Ireland");
    address.setPostCode("D08");

    IndividualPartyDetails individualPartyDetails = new IndividualPartyDetails();
    individualPartyDetails.setGivenName("GivenName");
    individualPartyDetails.setPartyKey("partykey");
    individualPartyDetails.setUpdateType("CustomerProvisioned");
    individualPartyDetails.setCurrentAddress(address);
    return individualPartyDetails;

  }

}
