package com.tenx.fraudamlmanager.onboarding.individual.domain;

import com.tenxbanking.party.event.CustomerEventV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "SEND_NON_PAYMENT_EVENT", havingValue = "false")
public class IndividualPartyEventServiceNoop implements IndividualPartyEventService {

  @Override
  public void processIndividualPartyEvent(CustomerEventV3 customerEventV3, String updateType) {
    log.info("Mock called since TMA notifications of non payment events are disabled.");
  }
}
