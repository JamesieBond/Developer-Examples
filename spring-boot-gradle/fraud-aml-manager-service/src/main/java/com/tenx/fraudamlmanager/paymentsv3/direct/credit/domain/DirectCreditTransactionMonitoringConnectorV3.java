package com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;

public interface DirectCreditTransactionMonitoringConnectorV3 {
    FraudCheckV3 sendFinCrimeCheck(DirectCreditPaymentV3 directCreditPaymentV3) throws TransactionMonitoringException;
}
