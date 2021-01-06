package com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain;

public interface DirectCreditTransactionMonitoringHandlerV2 {

  void checkDirectCreditV2(DirectCreditBacsPaymentV2 directCreditBacsPaymentV2)
      throws DirectCreditTransactionMonitoringExceptionV2;
}
