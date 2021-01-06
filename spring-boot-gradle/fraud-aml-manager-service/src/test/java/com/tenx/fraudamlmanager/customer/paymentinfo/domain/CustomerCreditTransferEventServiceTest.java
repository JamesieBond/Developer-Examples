package com.tenx.fraudamlmanager.customer.paymentinfo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tenx.fraudamlmanager.customer.paymentinfo.CustomerCreditTransferHelper;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.AcceptedCustomerCreditTransferRequest;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransfFeedzaiConnector;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferException;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.RejectedCustomerCreditTranferRequest;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CustomerCreditTransferEventServiceTest {

  @MockBean
  PaymentsDeviceProfileService paymentsDeviceProfileService;

  @MockBean
  CustomerCreditTransfFeedzaiConnector transfFeedzaiConnector;

  @SpyBean
  CustomerCreditTransferEventServiceImpl customerCreditTransferEventService;

  @Captor
  ArgumentCaptor<AcceptedCustomerCreditTransferRequest> acceptedCustomerCreditTransferRequestArgumentCaptor;

  @Captor
  ArgumentCaptor<RejectedCustomerCreditTranferRequest> rejectedCustomerCreditTranferRequestArgumentCaptor;


  @Test
  void processAcceptedCustomerCreditTransferTest() throws CustomerCreditTransferException, IOException {
    CustomerCreditTransferInitiationCompletedEvent event = CustomerCreditTransferHelper.readEventFromFile();
    event.getCustomerPaymentStatusReport().getOriginalGroupInformationAndStatus()
        .setGroupStatus("ACSC");

    PaymentDeviceProfile paymentDeviceProfile = new PaymentDeviceProfile();
    paymentDeviceProfile.setDeviceKeyId("keyID");

    BDDMockito.given(
        paymentsDeviceProfileService.fetchThreatMetrixResultUsingPartyKey("13tr8028-9825-4341-8600-4a2e159ff43b"))
        .willReturn(paymentDeviceProfile);

    customerCreditTransferEventService
        .processCustomerCreditTransferEvent(event, "2d94fc28-1d2b-4430-b2ce-0a5db9a3484a");

    Mockito.verify(
        transfFeedzaiConnector, Mockito.times(1))
        .sendCustomerCreditTransferCheck(acceptedCustomerCreditTransferRequestArgumentCaptor.capture());

    Mockito.verify(
        paymentsDeviceProfileService, Mockito.times(1))
        .fetchThreatMetrixResultUsingPartyKey("13tr8028-9825-4341-8600-4a2e159ff43b");

    assertEquals("keyID", acceptedCustomerCreditTransferRequestArgumentCaptor.getValue().getDeviceId());

    assertEquals("2d94fc28-1d2b-4430-b2ce-0a5db9a3484a",
        acceptedCustomerCreditTransferRequestArgumentCaptor.getValue().getTransactionTraceIdentification());


  }

  @Test
  void processRejectedCustomerCreditTransferTest() throws CustomerCreditTransferException, IOException {

    CustomerCreditTransferInitiationCompletedEvent event = CustomerCreditTransferHelper.readEventFromFile();

    PaymentDeviceProfile paymentDeviceProfile = new PaymentDeviceProfile();
    paymentDeviceProfile.setDeviceKeyId("keyID");

    BDDMockito.given(
        paymentsDeviceProfileService.fetchThreatMetrixResultUsingPartyKey("13tr8028-9825-4341-8600-4a2e159ff43b"))
        .willReturn(paymentDeviceProfile);

    customerCreditTransferEventService.processCustomerCreditTransferEvent(event, "abcdefgh");

    Mockito.verify(transfFeedzaiConnector, Mockito.times(1))
        .sendCustomerCreditTransferCheck(rejectedCustomerCreditTranferRequestArgumentCaptor.capture());

    assertEquals("keyID", rejectedCustomerCreditTranferRequestArgumentCaptor.getValue().getDeviceId());

    assertEquals("abcdefgh",
        rejectedCustomerCreditTranferRequestArgumentCaptor.getValue().getTransactionTraceIdentification());


  }
}