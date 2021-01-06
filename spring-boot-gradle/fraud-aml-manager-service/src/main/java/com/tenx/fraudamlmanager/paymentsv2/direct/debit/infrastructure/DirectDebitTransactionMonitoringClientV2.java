package com.tenx.fraudamlmanager.paymentsv2.direct.debit.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringConfig;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "directdebit-transaction-monitoring-v2", url = "${transactionmonitoring.url}",
    configuration = TransactionMonitoringConfig.class)
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "false", matchIfMissing = true)
public interface DirectDebitTransactionMonitoringClientV2 {

  /**
   * @param directDebitBacsPaymentV2 payload to pass
   */
  @PostMapping(value = "/v2/payments/directDebitFinCrimeCheck", consumes = "application/json")
  void checkFinCrimeV2DirectDebit(@RequestBody DirectDebitBacsPaymentV2 directDebitBacsPaymentV2)
      throws TransactionMonitoringException;


}
