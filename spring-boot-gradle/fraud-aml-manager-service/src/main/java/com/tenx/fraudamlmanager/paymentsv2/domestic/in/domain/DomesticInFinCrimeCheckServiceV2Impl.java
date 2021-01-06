package com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain;

import com.tenx.fraudamlmanager.paymentsv2.cases.domain.PaymentCaseServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomesticInFinCrimeCheckServiceV2Impl implements DomesticInFinCrimeCheckServiceV2 {

  private final DomesticInTransactionMonitoringServiceV2 domesticInTransactionMonitoringServiceV2;
  private final PaymentCaseServiceV2 paymentCaseServiceV2;

  public void checkFinCrimeV2(DomesticInPaymentV2 domesticInPaymentV2)
      throws DomesticInTransactionMonitoringExceptionV2 {

    domesticInTransactionMonitoringServiceV2.checkDomesticInPaymentV2(domesticInPaymentV2);

    paymentCaseServiceV2.createSavePaymentCase(domesticInPaymentV2);

  }

}
