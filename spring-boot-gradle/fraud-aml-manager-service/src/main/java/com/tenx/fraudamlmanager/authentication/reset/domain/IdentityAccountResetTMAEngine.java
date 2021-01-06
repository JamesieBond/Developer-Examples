package com.tenx.fraudamlmanager.authentication.reset.domain;

import com.tenx.fraudamlmanager.authentication.reset.infrastructure.AuthReset;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface IdentityAccountResetTMAEngine {

    void executeAuthReset(AuthReset authReset)
        throws TransactionMonitoringException;

}
