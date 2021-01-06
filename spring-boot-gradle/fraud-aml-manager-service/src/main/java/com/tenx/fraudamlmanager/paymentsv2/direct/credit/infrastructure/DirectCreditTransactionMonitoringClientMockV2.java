package com.tenx.fraudamlmanager.paymentsv2.direct.credit.infrastructure;

import com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain.DirectCreditBacsPaymentV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "true")
public class DirectCreditTransactionMonitoringClientMockV2 implements
    DirectCreditTransactionMonitoringClientV2 {

  /**
   * @param directCreditBacsPaymentV2 payload to pass
   */
  @PostMapping(value = "/v2/payments/directCreditFinCrimeCheck", consumes = "application/json")
  public void checkFinCrimeV2DirectCredit(@RequestBody DirectCreditBacsPaymentV2 directCreditBacsPaymentV2) {
    log.info("Mock TransactionMonitoring has been called for DirectCreditBacsPaymentV2");
  }
}
