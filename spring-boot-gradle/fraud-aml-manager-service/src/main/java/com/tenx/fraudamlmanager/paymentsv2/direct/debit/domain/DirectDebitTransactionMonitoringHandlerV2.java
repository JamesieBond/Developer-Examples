package com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain;

public interface DirectDebitTransactionMonitoringHandlerV2 {

    void checkDirectDebitV2(DirectDebitBacsPaymentV2 directDebitBacsPaymentV2)
        throws DirectDebitTransactionMonitoringExceptionV2;
}
