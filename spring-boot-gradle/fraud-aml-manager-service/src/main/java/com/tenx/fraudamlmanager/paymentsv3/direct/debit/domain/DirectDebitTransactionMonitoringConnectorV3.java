package com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;

public interface DirectDebitTransactionMonitoringConnectorV3 {
    FraudCheckV3 sendFinCrimeCheck(DirectDebitPaymentV3 directDebitPaymentV3) throws TransactionMonitoringException;
}
