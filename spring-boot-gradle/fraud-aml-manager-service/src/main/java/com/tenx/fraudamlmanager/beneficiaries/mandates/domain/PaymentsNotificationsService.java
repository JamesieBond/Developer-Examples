package com.tenx.fraudamlmanager.beneficiaries.mandates.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface PaymentsNotificationsService {

    void processMandatesBeneficiary(SetupMandatesDetails setupMandatesDetails)
        throws TransactionMonitoringException;

}
