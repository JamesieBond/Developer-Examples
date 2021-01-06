package com.tenx.fraudamlmanager.onboarding.payee.domain;

import com.tenx.payeemanager.event.payee.PayeesCreate;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import com.tenx.payeemanager.event.payee.PayeesUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "SEND_NON_PAYMENT_EVENT", havingValue = "false")
public class PayeesEventServiceNoop implements PayeesEventService {

  private static final String MOCK_CALLED = "Mock called since TMA notifications of non payment events are disabled";

  @Override
  public void processPayeeCreateEvent(PayeesCreate payeesCreate) {
    log.info(MOCK_CALLED);
  }

  @Override
  public void processPayeeUpdateEvent(PayeesUpdate payeesUpdate) {
    log.info(MOCK_CALLED);
  }

  @Override
  public void processPayeeDeleteEvent(PayeesDelete payeesDelete) {
    log.info(MOCK_CALLED);
  }
}
