package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.infrastructure;

import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@ConditionalOnProperty(value = "transactionmonitoring.enableMock", havingValue = "true")
public class DomesticOutReturnTransactionMonitoringClientMockV2 implements
    DomesticOutReturnTransactionMonitoringClientV2 {

  /**
   * @param domesticOutReturnPaymentV2 the payload to pass
   */
  @PostMapping(value = "/v2/payments/domesticPaymentOutboundReturnFinCrimeCheck", consumes = "application/json")
  public void postReturnPayment(@RequestBody DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2) {

  }

}
