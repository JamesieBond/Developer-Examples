package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain;

import com.tenx.fraudamlmanager.paymentsv2.cases.domain.PaymentCaseServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomesticOutReturnFinCrimeCheckServiceV2Impl implements DomesticOutReturnFinCrimeCheckServiceV2 {

  private final DomesticOutReturnTransactionMonitoringServiceV2 domesticOutReturnTransactionMonitoringServiceV2;
  private final PaymentCaseServiceV2 paymentCaseServiceV2;

  public void checkFinCrimeV2(DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2)
      throws DomesticOutReturnTransactionMonitoringExceptionV2 {

    domesticOutReturnTransactionMonitoringServiceV2.checkDomesticOutReturnPaymentV2(domesticOutReturnPaymentV2);

    paymentCaseServiceV2.createSavePaymentCase(domesticOutReturnPaymentV2);
  }

}
