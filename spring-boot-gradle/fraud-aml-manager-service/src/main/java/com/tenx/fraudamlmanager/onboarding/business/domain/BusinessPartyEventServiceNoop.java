package com.tenx.fraudamlmanager.onboarding.business.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "SEND_NON_PAYMENT_EVENT", havingValue = "false")
public class BusinessPartyEventServiceNoop implements BusinessPartyEventService {

  @Override
  public void processBusinessPartyEvent(BusinessPartyDetails businessPartyDetails) {
    log.info("Mock called since TMA notifications of non payment events are disabled.");
  }
}
