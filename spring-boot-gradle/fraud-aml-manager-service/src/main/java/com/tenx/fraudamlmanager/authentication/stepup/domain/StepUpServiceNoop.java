package com.tenx.fraudamlmanager.authentication.stepup.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(value = "SEND_NON_PAYMENT_EVENT", havingValue = "false")
public class StepUpServiceNoop implements StepUpService {

  @Override
  public void processStepUpEvent(StepUpDetails stepUpDetails) {
    log.info("Filtering StepUpDetails event partyKey {}", stepUpDetails.getPartyKey());

    if (stepUpDetails.isValidTmaPayment()) {
      log.info("Mock called since TMA notifications of non payment events are disabled.");
    } else {
      log.info("Mock called : StepUpDetails event with partyKey {} not applicable", stepUpDetails.getPartyKey());
    }
  }
}
