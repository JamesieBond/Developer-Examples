package com.tenx.fraudamlmanager.infrastructure.feedzaimanager;

import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.AcceptedCustomerCreditTransferRequest;
import com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.RejectedCustomerCreditTranferRequest;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.CreditTransferTransactionException;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs002;
import com.tenx.fraudamlmanager.payments.core.credittransfer.domain.model.Pacs008;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "feedzai-manager", url = "${feedzaimanager.url}", configuration = FeedzaiManagerConfig.class)
@ConditionalOnProperty(value = "feedzaimanager.enableMock", havingValue = "false", matchIfMissing = true)
public interface FeedzaiManagerClient {

  /**
   * @param pacs008 the payload to pass
   */
  @PostMapping(value = "/v1/iso/payments/customer-credit-transfer", consumes = "application/json")
  Pacs002 checkFinCrime(@RequestBody Pacs008 pacs008)
      throws CreditTransferTransactionException;


  @PostMapping(value = "v1/customer-credit-transfer-accepted")
  void checkCustomerCreditTransferStatus(
      @RequestBody AcceptedCustomerCreditTransferRequest acceptedCustomerCreditTransferRequest);

  @PostMapping(value = "v1/customer-credit-transfer-rejected")
  void checkCustomerCreditTransferStatus(
      @RequestBody RejectedCustomerCreditTranferRequest rejectedCustomerCreditTranferRequest);


}
