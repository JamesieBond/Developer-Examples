package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tenx.fraudamlmanager.infrastructure.feedzaimanager.FeedzaiManagerClient;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CustomerCreditTransfFeedzaiConnectorTest {

  @MockBean
  CustomerCreditTransferMetrics customerCreditTransferMetrics;

  @MockBean
  FeedzaiManagerClient feedzaiManagerClient;

  @SpyBean
  CustomerCreditTransfFeedzaiConnector customerCreditTransfFeedzaiConnector;


  @Test
  void testSuccessFullAcceptedCall() throws CustomerCreditTransferException {
    AcceptedCustomerCreditTransferRequest acceptedCustomerCreditTransferRequest = new AcceptedCustomerCreditTransferRequest();
    customerCreditTransfFeedzaiConnector.sendCustomerCreditTransferCheck(acceptedCustomerCreditTransferRequest);
    Mockito.verify(feedzaiManagerClient, Mockito.times(1))
        .checkCustomerCreditTransferStatus(acceptedCustomerCreditTransferRequest);
    Mockito.verify(customerCreditTransferMetrics, Mockito.times(1)).incrementAcceptedTotalCounter();
    Mockito.verify(customerCreditTransferMetrics, Mockito.times(0)).incrementAcceptedFailedCounter();
  }


  @Test
  void testFailedAcceptedCall() throws CustomerCreditTransferException {
    AcceptedCustomerCreditTransferRequest acceptedCustomerCreditTransferRequest = new AcceptedCustomerCreditTransferRequest();

    Mockito.doThrow(FeignException.class).when(feedzaiManagerClient)
        .checkCustomerCreditTransferStatus(acceptedCustomerCreditTransferRequest);

    assertThrows(CustomerCreditTransferException.class, () -> customerCreditTransfFeedzaiConnector
        .sendCustomerCreditTransferCheck(acceptedCustomerCreditTransferRequest));

    Mockito.verify(customerCreditTransferMetrics, Mockito.times(1)).incrementAcceptedTotalCounter();
    Mockito.verify(customerCreditTransferMetrics, Mockito.times(1)).incrementAcceptedFailedCounter();
  }

  @Test
  void testSuccessfullRejectedCall() throws CustomerCreditTransferException {
    RejectedCustomerCreditTranferRequest rejectedCustomerCreditTranferRequest = new RejectedCustomerCreditTranferRequest();
    customerCreditTransfFeedzaiConnector.sendCustomerCreditTransferCheck(rejectedCustomerCreditTranferRequest);
    Mockito.verify(feedzaiManagerClient, Mockito.times(1))
        .checkCustomerCreditTransferStatus(rejectedCustomerCreditTranferRequest);
    Mockito.verify(customerCreditTransferMetrics, Mockito.times(1)).incrementRejectedTotalCounter();
    Mockito.verify(customerCreditTransferMetrics, Mockito.times(0)).incrementRejectedFailedCounter();
  }


  @Test
  void testFailedlRejectedCall() throws CustomerCreditTransferException {
    RejectedCustomerCreditTranferRequest rejectedCustomerCreditTranferRequest = new RejectedCustomerCreditTranferRequest();
    Mockito.doThrow(FeignException.class).when(feedzaiManagerClient)
        .checkCustomerCreditTransferStatus(rejectedCustomerCreditTranferRequest);

    assertThrows(CustomerCreditTransferException.class, () -> customerCreditTransfFeedzaiConnector
        .sendCustomerCreditTransferCheck(rejectedCustomerCreditTranferRequest));
    Mockito.verify(customerCreditTransferMetrics, Mockito.times(1)).incrementRejectedTotalCounter();
    Mockito.verify(customerCreditTransferMetrics, Mockito.times(1)).incrementRejectedFailedCounter();
  }

}