package com.tenx.fraudamlmanager.authentication.loginattempts.domain;

import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttempts;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface LoginAttemptsTMAEngine {

    void executeLoginAttempts(LoginAttempts loginAttempts)
        throws TransactionMonitoringException;

}
