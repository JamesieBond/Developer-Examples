package com.tenx.fraudamlmanager.paymentsv2.domestic.in.infrastructure;

import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "true")
public class DomesticInTransactionMonitoringClientMockV2 implements
        DomesticInTransactionMonitoringClientV2 {

  /**
   * @param domesticInPaymentV2 the payload to pass
   */
  @PostMapping(value = "/v2/payments/domesticPaymentInboundFinCrimeCheck", consumes = "application/json")
  public void checkFinCrimeV2(@RequestBody DomesticInPaymentV2 domesticInPaymentV2) {
    log.info("Mock TransactionMonitoring has been called for DomesticInPaymentV2");
  }

}
