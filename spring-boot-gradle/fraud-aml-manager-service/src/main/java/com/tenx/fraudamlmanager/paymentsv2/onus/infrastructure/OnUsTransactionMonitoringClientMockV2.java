package com.tenx.fraudamlmanager.paymentsv2.onus.infrastructure;

import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "true")
public class OnUsTransactionMonitoringClientMockV2 implements
  OnUsTransactionMonitoringClientV2 {

  /**
   * @param onUsPaymentV2 the payload to pass
   */
  @PostMapping(value = "/v2/payments/onUsFinCrimeCheck", consumes = "application/json")
  public void checkFinCrimeV2(@RequestBody OnUsPaymentV2 onUsPaymentV2) {

  }

}
