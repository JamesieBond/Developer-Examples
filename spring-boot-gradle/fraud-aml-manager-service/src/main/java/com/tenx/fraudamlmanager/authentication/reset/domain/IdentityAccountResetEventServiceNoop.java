package com.tenx.fraudamlmanager.authentication.reset.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(value = "SEND_NON_PAYMENT_EVENT", havingValue = "false")
public class IdentityAccountResetEventServiceNoop implements IdentityAccountResetEventService {

  @Override
  public void processIdentityAccountResetEvent(AuthResetDetails authResetDetails) {

    log.info("Filtering AuthResetDetails partyKey {}", authResetDetails.getPartyKey());
    if (authResetDetails.isIdentityAccountResetApplicable()) {
      log.info("Mock called since TMA notifications of non payment events are disabled.");
    } else {
      log.info("Mock called : AuthResetDetails with partyKey {} not applicable", authResetDetails.getPartyKey());
    }
  }
}
