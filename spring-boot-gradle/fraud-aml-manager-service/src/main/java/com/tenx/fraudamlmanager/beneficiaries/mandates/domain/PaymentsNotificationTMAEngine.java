package com.tenx.fraudamlmanager.beneficiaries.mandates.domain;

import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.SetupMandates;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface PaymentsNotificationTMAEngine {

    void executePaymentNotification(SetupMandates setupMandates)
        throws TransactionMonitoringException;

}
