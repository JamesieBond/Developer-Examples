package com.tenx.fraudamlmanager.paymentsv3.onus.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;

public interface OnUsTransactionMonitoringConnector {

    FraudCheckV3 checkFinCrimeV3(OnUsPaymentV3 onUsPaymentV3) throws TransactionMonitoringException;
}
