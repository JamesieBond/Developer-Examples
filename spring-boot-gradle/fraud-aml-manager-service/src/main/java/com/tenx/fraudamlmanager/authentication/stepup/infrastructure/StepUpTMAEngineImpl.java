package com.tenx.fraudamlmanager.authentication.stepup.infrastructure;

import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpTMAEngine;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StepUpTMAEngineImpl implements StepUpTMAEngine {

  private final StepUpEventMetrics stepUpEventMetrics;
  private final TransactionMonitoringClient transactionMonitoringClient;

  public void executeStepUp(StepUpPayload stepUpPayload)
      throws TransactionMonitoringException {
    try {
      transactionMonitoringClient.sendStepUpEvent(stepUpPayload);
      stepUpEventMetrics.incrementFAMStepUpRequestsToTMASuccess();
      log.info("Sent StepUp event with partyKey {} to transaction monitoring client",
          stepUpPayload.getPartyKey());
    } catch (TransactionMonitoringException e) {
      stepUpEventMetrics.incrementFAMStepUpRequestsToTMAFailed();
      log.error("Unable to send StepUp event with partyKey {} to transaction monitoring client",
          stepUpPayload.getPartyKey(), e);
      throw e;
    }
  }
}
