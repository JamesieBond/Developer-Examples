package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;

public interface domesticTransactionMonitoringConnector {

    FraudCheckV3 sendFinCrimeCheck(DomesticInPaymentV3 inPaymentV3) throws TransactionMonitoringException;

    FraudCheckV3 sendFinCrimeCheck(DomesticOutPaymentV3 outPaymentV3) throws TransactionMonitoringException;

    FraudCheckV3 sendFinCrimeCheck(DomesticOutReturnPaymentV3 outReturnPaymentV3) throws TransactionMonitoringException;

}
