package com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain;

public interface DirectDebitFinCrimeCheckServiceV2 {

    void checkFinCrimeV2(DirectDebitBacsPaymentV2 directDebitPayment) throws
        DirectDebitTransactionMonitoringExceptionV2;

}
