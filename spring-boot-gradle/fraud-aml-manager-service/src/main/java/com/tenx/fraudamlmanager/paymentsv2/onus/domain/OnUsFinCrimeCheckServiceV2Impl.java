package com.tenx.fraudamlmanager.paymentsv2.onus.domain;

import com.tenx.fraudamlmanager.paymentsv2.cases.domain.PaymentCaseServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnUsFinCrimeCheckServiceV2Impl implements OnUsFinCrimeCheckServiceV2 {

  private final OnUsTransactionMonitoringServiceV2 onUsTransactionMonitoringServiceV2;
  private final PaymentCaseServiceV2 paymentCaseServiceV2;

  public void checkFinCrimeV2(OnUsPaymentV2 onUsPaymentV2)
    throws OnUsTransactionMonitoringExceptionV2 {

    onUsTransactionMonitoringServiceV2.checkOnUsPaymentV2(onUsPaymentV2);

    paymentCaseServiceV2.createSavePaymentCase(onUsPaymentV2);

  }
}
