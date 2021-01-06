package com.tenx.fraudamlmanager.paymentsv2.domestic.in.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringConfig;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "in-transaction-monitoring-v2", url = "${transactionmonitoring.url}", configuration = TransactionMonitoringConfig.class)
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "false", matchIfMissing = true)
public interface DomesticInTransactionMonitoringClientV2 {

  /**
   * @param domesticInPaymentV2 the payload to pass
   */
  @PostMapping(value = "/v2/payments/domesticPaymentInboundFinCrimeCheck", consumes = "application/json")
  void checkFinCrimeV2(@RequestBody DomesticInPaymentV2 domesticInPaymentV2)
          throws TransactionMonitoringException;

}