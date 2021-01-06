package com.tenx.fraudamlmanager.paymentsv2.onus.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringConfig;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "onus-transaction-monitoring-v2", url = "${transactionmonitoring.url}", configuration = TransactionMonitoringConfig.class)
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "false", matchIfMissing = true)
public interface OnUsTransactionMonitoringClientV2 {

  /**
   * @param onUsPaymentV2 the payload to pass
   */
  @PostMapping(value = "/v2/payments/onUsFinCrimeCheck", consumes = "application/json")
  void checkFinCrimeV2(@RequestBody OnUsPaymentV2 onUsPaymentV2)
    throws TransactionMonitoringException;

}