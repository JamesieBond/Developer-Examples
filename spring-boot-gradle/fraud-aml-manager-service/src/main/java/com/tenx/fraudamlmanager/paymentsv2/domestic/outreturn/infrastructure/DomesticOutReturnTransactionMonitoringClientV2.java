package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringConfig;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "outreturn-transaction-monitoring-v2", url = "${transactionmonitoring.url}", configuration = TransactionMonitoringConfig.class)
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "false", matchIfMissing = true)
public interface DomesticOutReturnTransactionMonitoringClientV2 {

  /**
   * @param domesticOutReturnPaymentV2 the payload to pass
   */
  @PostMapping(value = "/v2/payments/domesticPaymentOutboundReturnFinCrimeCheck", consumes = "application/json")
  void postReturnPayment(@RequestBody DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2)
      throws TransactionMonitoringException;

}
