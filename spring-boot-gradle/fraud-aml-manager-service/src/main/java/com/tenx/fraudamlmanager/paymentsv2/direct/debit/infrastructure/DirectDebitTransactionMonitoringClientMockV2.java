package com.tenx.fraudamlmanager.paymentsv2.direct.debit.infrastructure;

import com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain.DirectDebitBacsPaymentV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "true")
public class DirectDebitTransactionMonitoringClientMockV2 implements
    DirectDebitTransactionMonitoringClientV2 {

  /**
   * @param directDebitBacsPaymentV2 payload to pass
   */
  @PostMapping(value = "/v2/payments/directDebitFinCrimeCheck", consumes = "application/json")
  public void checkFinCrimeV2DirectDebit(@RequestBody DirectDebitBacsPaymentV2 directDebitBacsPaymentV2) {
    log.info("Mock TransactionMonitoring has been called for DirectDebitBacsPaymentV2");
  }

}
