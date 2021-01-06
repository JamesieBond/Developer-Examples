package com.tenx.fraudamlmanager.paymentsv2.onus.domain;

public interface OnUsTransactionMonitoringServiceV2 {

  void checkOnUsPaymentV2(OnUsPaymentV2 onUsPaymentV2) throws OnUsTransactionMonitoringExceptionV2;
}