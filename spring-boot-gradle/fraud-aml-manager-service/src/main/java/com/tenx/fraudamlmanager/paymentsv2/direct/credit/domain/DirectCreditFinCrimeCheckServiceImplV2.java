package com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectCreditFinCrimeCheckServiceImplV2 implements DirectCreditFinCrimeCheckServiceV2 {

  private final DirectCreditTransactionMonitoringHandlerV2 directCreditTransactionMonitoringHandlerV2;

  public void checkFinCrimeDirectCreditV2(DirectCreditBacsPaymentV2 directCreditBacsPaymentV2)
      throws DirectCreditTransactionMonitoringExceptionV2 {

    directCreditTransactionMonitoringHandlerV2.checkDirectCreditV2(directCreditBacsPaymentV2);
  }

}
