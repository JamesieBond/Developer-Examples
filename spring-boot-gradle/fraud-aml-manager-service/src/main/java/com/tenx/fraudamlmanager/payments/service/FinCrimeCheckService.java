package com.tenx.fraudamlmanager.payments.service;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.payments.model.api.DirectCreditPayment;
import com.tenx.fraudamlmanager.payments.model.api.DirectDebitPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsInboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsOutboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.api.OnUsPayment;

public interface FinCrimeCheckService {
    FraudCheckResponse checkFinCrime(OnUsPayment onUsPayment) throws TransactionMonitoringException;

    FraudCheckResponse checkFinCrime(FpsOutboundPayment fpsOutboundPayment) throws TransactionMonitoringException;

    FraudCheckResponse checkFinCrime(FpsInboundPayment fpsInboundPayment) throws TransactionMonitoringException;

    FraudCheckResponse checkFinCrime(DirectDebitPayment directDebitPayment) throws TransactionMonitoringException;

    FraudCheckResponse checkFinCrime(DirectCreditPayment directCreditPayment) throws TransactionMonitoringException;


}
