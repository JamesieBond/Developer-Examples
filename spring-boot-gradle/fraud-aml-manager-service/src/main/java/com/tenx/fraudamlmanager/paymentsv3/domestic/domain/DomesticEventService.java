package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

/**
 * @author Niall O'Connell
 */
public interface DomesticEventService {

    void produceEventForFinCrime(DomesticInPaymentV3 domesticInPaymentV3) throws TransactionMonitoringException;

    void produceEventForFinCrime(DomesticOutPaymentV3 domesticInPaymentV3, String deviceId) throws TransactionMonitoringException;

    void produceEventForFinCrime(DomesticOutReturnPaymentV3 domesticInPaymentV3) throws TransactionMonitoringException;
}
