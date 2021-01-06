package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain;

public interface DomesticOutReturnTransactionMonitoringServiceV2 {

  void checkDomesticOutReturnPaymentV2(DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2)
      throws DomesticOutReturnTransactionMonitoringExceptionV2;

}
