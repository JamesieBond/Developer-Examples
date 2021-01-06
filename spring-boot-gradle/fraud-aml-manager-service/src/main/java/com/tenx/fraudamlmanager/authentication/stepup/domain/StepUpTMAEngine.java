package com.tenx.fraudamlmanager.authentication.stepup.domain;

import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpPayload;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface StepUpTMAEngine {

    void executeStepUp(StepUpPayload stepUpPayload)
        throws TransactionMonitoringException;

}
