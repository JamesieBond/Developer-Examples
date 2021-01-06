package com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;

public interface DirectDebitFinCrimeCheckServiceV3 {

    FraudCheckV3 checkFinCrimeV3(DirectDebitPaymentV3 directDebitPaymentV3)
            throws TransactionMonitoringException;
}
