package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture;

import com.tenx.fraudamlmanager.infrastructure.feedzaimanager.FeedzaiManagerClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerCreditTransfFeedzaiConnector {

  private final FeedzaiManagerClient feedzaiManagerClient;

  private final CustomerCreditTransferMetrics customerCreditTransferMetrics;

  private final String MESSAGE_MAKING_CALL_TO_FZM = "Calling FZM customer-credit-transfer-{} for message with transactionTraceIdentification: {}. ";

  private final String MESSAGE_EXCEPTION_MAKING_FZM_CALL = "Exception encountered when calling FZM customer-credit-transfer-{} for message with transactionTraceIdentification: {}. Cause is : {}";

  public void sendCustomerCreditTransferCheck(
      AcceptedCustomerCreditTransferRequest acceptedCustomerCreditTransferRequest)
      throws CustomerCreditTransferException {
    log.info(MESSAGE_MAKING_CALL_TO_FZM, "accepted",
        acceptedCustomerCreditTransferRequest.getTransactionTraceIdentification());
    customerCreditTransferMetrics.incrementAcceptedTotalCounter();
    try {
      feedzaiManagerClient.checkCustomerCreditTransferStatus(acceptedCustomerCreditTransferRequest);
    } catch (FeignException ex) {
      customerCreditTransferMetrics.incrementAcceptedFailedCounter();
      log.error(
          MESSAGE_EXCEPTION_MAKING_FZM_CALL, "accepted",
          acceptedCustomerCreditTransferRequest.getTransactionTraceIdentification(), ex.getMessage());
      throw new CustomerCreditTransferException(ex.status(), ex.getMessage(), ex);
    }
  }


  public void sendCustomerCreditTransferCheck(
      RejectedCustomerCreditTranferRequest rejectedCustomerCreditTranferRequest)
      throws CustomerCreditTransferException {
    log.info(MESSAGE_MAKING_CALL_TO_FZM, "rejected",
        rejectedCustomerCreditTranferRequest.getTransactionTraceIdentification());
    customerCreditTransferMetrics.incrementRejectedTotalCounter();
    try {
      feedzaiManagerClient.checkCustomerCreditTransferStatus(rejectedCustomerCreditTranferRequest);
    } catch (FeignException ex) {
      customerCreditTransferMetrics.incrementRejectedFailedCounter();
      log.error(
          MESSAGE_EXCEPTION_MAKING_FZM_CALL, "rejected",
          rejectedCustomerCreditTranferRequest.getTransactionTraceIdentification(), ex.getMessage());
      throw new CustomerCreditTransferException(ex.status(), ex.getMessage(), ex);
    }
  }
}
