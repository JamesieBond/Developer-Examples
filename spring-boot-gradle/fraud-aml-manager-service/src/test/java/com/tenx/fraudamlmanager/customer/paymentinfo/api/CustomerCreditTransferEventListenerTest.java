package com.tenx.fraudamlmanager.customer.paymentinfo.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.tenx.fraudamlmanager.customer.paymentinfo.CustomerCreditTransferHelper;
import com.tenx.fraudamlmanager.customer.paymentinfo.domain.CustomerCreditTransferEventService;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransfFeedzaiConnector;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.CustomerCreditTransferException;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentDeviceProfile;
import com.tenx.fraudamlmanager.deviceprofile.domain.PaymentsDeviceProfileService;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class CustomerCreditTransferEventListenerTest {

  @Mock
  private Acknowledgment acknowledgment;

  @MockBean
  PaymentsDeviceProfileService paymentsDeviceProfileService;

  @MockBean
  CustomerCreditTransfFeedzaiConnector customerCreditTransfFeedzaiConnector;

  @MockBean
  private CustomerCreditTransferEventService customerCreditTransferEventService;

  private CustomerCreditTransferEventListener listener;

  @Captor
  private ArgumentCaptor<CustomerCreditTransferInitiationCompletedEvent> cctiEventArgumentCaptor;

  @Captor
  private ArgumentCaptor<String> transactionTraceIdCaptor;

  private CustomerCreditTransferInitiationCompletedEvent cctiCompletedEvent;

  @BeforeEach
  private void instantiateObjects() throws IOException {
    listener = new CustomerCreditTransferEventListener(customerCreditTransferEventService);
    cctiCompletedEvent = CustomerCreditTransferHelper.readEventFromFile();
  }

  @Test
  void testRejectedCusstomerCreditTransfer() throws CustomerCreditTransferException {
    ConsumerRecord<String, CustomerCreditTransferInitiationCompletedEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", cctiCompletedEvent);

    PaymentDeviceProfile paymentsDeviceProfile = new PaymentDeviceProfile();
    paymentsDeviceProfile.setDeviceKeyId("keyID");
    BDDMockito.given(paymentsDeviceProfileService.fetchThreatMetrixResultUsingPartyKey("my_test_party_key"))
        .willReturn(paymentsDeviceProfile);

    listener.handleCustomerCreditTransferInitiationCompletedEvent(consumerRecord, acknowledgment);

    Mockito.verify(customerCreditTransferEventService, atLeastOnce())
        .processCustomerCreditTransferEvent(cctiEventArgumentCaptor.capture(), transactionTraceIdCaptor.capture());

    Mockito.verify(acknowledgment, atLeastOnce()).acknowledge();

    assertEquals("2d94fc28-1d2b-4430-b2ce-0a5db9a3484a", transactionTraceIdCaptor.getValue());


  }


  @Test
  void testAcceptCustomerCreditTransfer() throws CustomerCreditTransferException {
    cctiCompletedEvent.getCustomerPaymentStatusReport().getOriginalGroupInformationAndStatus()
        .setGroupStatus("ACSC");

    ConsumerRecord<String, CustomerCreditTransferInitiationCompletedEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", cctiCompletedEvent);

    PaymentDeviceProfile paymentsDeviceProfile = new PaymentDeviceProfile();
    paymentsDeviceProfile.setDeviceKeyId("keyID");
    BDDMockito.given(paymentsDeviceProfileService.fetchThreatMetrixResultUsingPartyKey("my_test_party_key"))
        .willReturn(paymentsDeviceProfile);

    listener.handleCustomerCreditTransferInitiationCompletedEvent(consumerRecord, acknowledgment);

    Mockito.verify(customerCreditTransferEventService, atLeastOnce())
        .processCustomerCreditTransferEvent(cctiEventArgumentCaptor.capture(), transactionTraceIdCaptor.capture());

    Mockito.verify(acknowledgment, atLeastOnce()).acknowledge();

    assertEquals("2d94fc28-1d2b-4430-b2ce-0a5db9a3484a", transactionTraceIdCaptor.getValue());


  }


  @Test
  void testInvalidEventReceived() throws CustomerCreditTransferException {
    cctiCompletedEvent.getCustomerPaymentStatusReport().setOriginalPaymentInformationAndStatus(new ArrayList<>());
    ConsumerRecord<String, CustomerCreditTransferInitiationCompletedEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", cctiCompletedEvent);

    doThrow(IllegalArgumentException.class)
        .when(customerCreditTransferEventService).processCustomerCreditTransferEvent(any(), anyString());

    Mockito.verify(acknowledgment, never()).acknowledge();

  }

  @Test
  void testUnknownGroupStatus() throws CustomerCreditTransferException {
    cctiCompletedEvent.getCustomerPaymentStatusReport().getOriginalGroupInformationAndStatus()
        .setGroupStatus("SMTH");
    ConsumerRecord<String, CustomerCreditTransferInitiationCompletedEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", cctiCompletedEvent);

    listener.handleCustomerCreditTransferInitiationCompletedEvent(consumerRecord, acknowledgment);

    Mockito.verify(customerCreditTransferEventService, times(1))
        .processCustomerCreditTransferEvent(any(), anyString());

    Mockito.verify(acknowledgment, times(1)).acknowledge();

  }


  @Test
  void testAcceptedTypeException() throws CustomerCreditTransferException {
    cctiCompletedEvent.getCustomerPaymentStatusReport().getOriginalGroupInformationAndStatus()
        .setGroupStatus("ACSC");
    ConsumerRecord<String, CustomerCreditTransferInitiationCompletedEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", cctiCompletedEvent);

    doThrow(CustomerCreditTransferException.class).when(customerCreditTransferEventService)
        .processCustomerCreditTransferEvent(any(), anyString());

    assertThrows(CustomerCreditTransferException.class,
        () -> listener.handleCustomerCreditTransferInitiationCompletedEvent(consumerRecord, acknowledgment));
    Mockito.verify(acknowledgment, times(0)).acknowledge();

  }

  @Test
  void testRejectedTypeException() throws CustomerCreditTransferException {
    ConsumerRecord<String, CustomerCreditTransferInitiationCompletedEvent> consumerRecord =
        new ConsumerRecord<>("topic", 0, 0, "key", cctiCompletedEvent);

    doThrow(CustomerCreditTransferException.class).when(customerCreditTransferEventService)
        .processCustomerCreditTransferEvent(any(), anyString());

    assertThrows(CustomerCreditTransferException.class,
        () -> listener.handleCustomerCreditTransferInitiationCompletedEvent(consumerRecord, acknowledgment));
    Mockito.verify(acknowledgment, times(0)).acknowledge();

  }

}