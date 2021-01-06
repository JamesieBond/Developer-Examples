package com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain;

public interface DomesticInTransactionMonitoringServiceV2 {

  void checkDomesticInPaymentV2(DomesticInPaymentV2 domesticInPaymentV2) throws DomesticInTransactionMonitoringExceptionV2;

}
