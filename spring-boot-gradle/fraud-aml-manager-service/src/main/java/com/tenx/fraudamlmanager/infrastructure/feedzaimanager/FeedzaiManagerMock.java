package com.tenx.fraudamlmanager.infrastructure.feedzaimanager;

import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.AcceptedCustomerCreditTransferRequest;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.RejectedCustomerCreditTranferRequest;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.CreditTransferTransaction39;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryData1;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.SupplementaryDataEnvelope1;
import feign.FeignException;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "feedzaimanager.enableMock", havingValue = "true")
public class FeedzaiManagerMock implements FeedzaiManagerClient {

  /**
   * @param pacs008 the payload to pass
   */
  @Override
  public Pacs002 checkFinCrime(Pacs008 pacs008) throws FeignException {
    log.info("CheckFinCrime for transactionId {} mocked", pacs008.getCustomerCreditTransfer()
        .getCreditTransferFraudCheckRequest().getCreditTransferTransactionInformation().stream()
        .filter(Objects::nonNull)
        .map(CreditTransferTransaction39::getSupplementaryData)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(SupplementaryData1::getEnvelope)
        .filter(Objects::nonNull)
        .map(SupplementaryDataEnvelope1::getTransactionTraceIdentification)
        .filter(Objects::nonNull)
        .findFirst().orElse(""));
    return new Pacs002();
  }

  @Override
  public void checkCustomerCreditTransferStatus(
      AcceptedCustomerCreditTransferRequest acceptedCustomerCreditTransferRequest) {
    log.info("Check Customer Credit Accepted status call to Feedzai mocked for {}",
        acceptedCustomerCreditTransferRequest);
  }

  @Override
  public void checkCustomerCreditTransferStatus(
      RejectedCustomerCreditTranferRequest rejectedCustomerCreditTranferRequest) {
    log.info("Check Customer Credit Rejected status call to Feedzai mocked for {}",
        rejectedCustomerCreditTranferRequest);
  }
}
