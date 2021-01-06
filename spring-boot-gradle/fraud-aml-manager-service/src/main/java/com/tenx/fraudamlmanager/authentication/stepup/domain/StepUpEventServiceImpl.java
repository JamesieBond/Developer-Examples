package com.tenx.fraudamlmanager.authentication.stepup.domain;

import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpDetailsMapper;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpPayload;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "SEND_NON_PAYMENT_EVENT", matchIfMissing = true)
public class StepUpEventServiceImpl implements StepUpService {

    private final StepUpTMAEngine stepUpTMAEngine;

    public void processStepUpEvent(StepUpDetails stepUpDetails)
        throws TransactionMonitoringException {
        log.info("Filtering StepUpDetails event partyKey {}", stepUpDetails.getPartyKey());

        if (stepUpDetails.isValidTmaPayment()) {
            StepUpPayload stepUpPayload = StepUpDetailsMapper.MAPPER.mapToStepUp(stepUpDetails);
            stepUpTMAEngine.executeStepUp(stepUpPayload);
        } else {
            log.info("StepUpDetails event with partyKey {} not applicable", stepUpDetails.getPartyKey());
        }
    }
}
