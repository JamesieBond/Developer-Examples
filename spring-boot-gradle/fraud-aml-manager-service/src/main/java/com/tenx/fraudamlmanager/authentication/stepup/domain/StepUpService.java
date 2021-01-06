package com.tenx.fraudamlmanager.authentication.stepup.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface StepUpService {

    void processStepUpEvent(StepUpDetails stepUpDetails)
        throws TransactionMonitoringException;

}
