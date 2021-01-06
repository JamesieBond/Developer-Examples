package com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;

/**
 * @author Niall O'Connell
 */
public interface DirectCreditFinCrimeCheckServiceV3 {

    FraudCheckV3 checkFinCrimeV3(DirectCreditPaymentV3 directCreditPaymentV3) throws TransactionMonitoringException;
}
