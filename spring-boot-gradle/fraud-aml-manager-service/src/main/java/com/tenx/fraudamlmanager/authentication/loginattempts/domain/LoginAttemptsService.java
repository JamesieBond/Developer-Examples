package com.tenx.fraudamlmanager.authentication.loginattempts.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface LoginAttemptsService {

    void processLoginAttemptsEvent(LoginAttemptsDetails loginAttemptsDetails)
        throws TransactionMonitoringException;

}
