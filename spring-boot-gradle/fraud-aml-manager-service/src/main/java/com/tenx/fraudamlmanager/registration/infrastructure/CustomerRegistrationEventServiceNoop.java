package com.tenx.fraudamlmanager.registration.infrastructure;

import com.tenx.security.forgerockfacade.resource.CustomerRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "SEND_NON_PAYMENT_EVENT", havingValue = "false")
public class CustomerRegistrationEventServiceNoop implements CustomerRegistrationEventService {

  @Override
  public void processCustomerRegistrationEvent(CustomerRegistration customerRegistration) {
    log.info("Mock called since TMA notifications of non payment events are disabled.");
  }
}
