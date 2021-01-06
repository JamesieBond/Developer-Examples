package com.tenx.fraudamlmanager.paymentsv3.onus.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudCheckV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;

/**
 * @author Niall O'Connell
 */
public interface OnUsFinCrimeCheckServiceV3 {

    FraudCheckV3 checkFinCrimeV3(OnUsPaymentV3 onUsPaymentV3, String deviceKeyId)
            throws TransactionMonitoringException;
}
