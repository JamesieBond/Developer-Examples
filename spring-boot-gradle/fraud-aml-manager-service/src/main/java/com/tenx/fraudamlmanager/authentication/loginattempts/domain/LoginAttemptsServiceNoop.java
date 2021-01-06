package com.tenx.fraudamlmanager.authentication.loginattempts.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(value = "SEND_NON_PAYMENT_EVENT", havingValue = "false")
public class LoginAttemptsServiceNoop implements LoginAttemptsService {

  @Override
  public void processLoginAttemptsEvent(LoginAttemptsDetails loginAttemptsDetails) {
    log.info("Filtering LoginAttemptsDetails event partyKey {}", loginAttemptsDetails.getPartyKey());
    if (loginAttemptsDetails.isValidTmaPayment()) {
      log.info("Mock called since TMA notifications of non payment events are disabled.");
    } else {
      log.info("Mock called : LoginAttemptsDetails event with partyKey {} not applicable", loginAttemptsDetails.getPartyKey());
    }
  }
}
