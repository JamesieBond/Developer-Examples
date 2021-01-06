package com.tenx.fraudamlmanager.authentication.reset.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface IdentityAccountResetEventService {

    void processIdentityAccountResetEvent(AuthResetDetails authResetDetails)
        throws TransactionMonitoringException;

}
