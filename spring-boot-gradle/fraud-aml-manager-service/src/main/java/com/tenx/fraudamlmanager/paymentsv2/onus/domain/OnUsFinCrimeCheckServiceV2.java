package com.tenx.fraudamlmanager.paymentsv2.onus.domain;


public interface OnUsFinCrimeCheckServiceV2 {

  void checkFinCrimeV2(OnUsPaymentV2 onUsPaymentV2)
    throws OnUsTransactionMonitoringExceptionV2;

}
