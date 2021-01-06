package com.tenx.fraudamlmanager.paymentsv2.direct.credit.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringConfig;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "directcredit-transaction-monitoring-v2", url = "${transactionmonitoring.url}",
    configuration = TransactionMonitoringConfig.class)
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "false", matchIfMissing = true)
public interface DirectCreditTransactionMonitoringClientV2 {

  /**
   * @param directCreditBacsPaymentV2 payload to pass
   */
  @PostMapping(value = "/v2/payments/directCreditFinCrimeCheck", consumes = "application/json")
  void checkFinCrimeV2DirectCredit(@RequestBody DirectCreditBacsPaymentV2 directCreditBacsPaymentV2)
      throws TransactionMonitoringException;

}
