package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain;


public interface DomesticOutReturnFinCrimeCheckServiceV2 {

  void checkFinCrimeV2(DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2)
      throws DomesticOutReturnTransactionMonitoringExceptionV2;

}
