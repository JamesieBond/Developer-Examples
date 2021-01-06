package com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain;

public interface DirectCreditFinCrimeCheckServiceV2 {

  void checkFinCrimeDirectCreditV2(DirectCreditBacsPaymentV2 directCreditBacsPaymentV2) throws
      DirectCreditTransactionMonitoringExceptionV2;

}
