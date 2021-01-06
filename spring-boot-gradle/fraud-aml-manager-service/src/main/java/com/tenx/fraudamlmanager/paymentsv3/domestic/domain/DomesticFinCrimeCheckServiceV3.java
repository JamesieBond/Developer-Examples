package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;

/**
 * @author Niall O'Connell
 */
public interface DomesticFinCrimeCheckServiceV3 {

    FraudCheckV3 checkFinCrimeV3(DomesticOutPaymentV3 domesticOutPaymentV3, String deviceKeyId)
            throws TransactionMonitoringException;

    FraudCheckV3 checkFinCrimeV3(DomesticInPaymentV3 domesticInPaymentV3)
            throws TransactionMonitoringException;

    FraudCheckV3 checkFinCrimeV3(DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3)
            throws TransactionMonitoringException;

}
