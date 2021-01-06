package com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain;

public interface DomesticInFinCrimeCheckServiceV2 {

  void checkFinCrimeV2(DomesticInPaymentV2 domesticInPaymentV2) throws DomesticInTransactionMonitoringExceptionV2;
}
