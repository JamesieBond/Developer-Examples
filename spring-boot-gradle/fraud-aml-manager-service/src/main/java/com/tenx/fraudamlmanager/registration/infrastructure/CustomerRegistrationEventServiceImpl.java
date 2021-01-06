package com.tenx.fraudamlmanager.registration.infrastructure;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.CustomerRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "SEND_NON_PAYMENT_EVENT", matchIfMissing = true)
public class CustomerRegistrationEventServiceImpl implements CustomerRegistrationEventService {

  private final CustomerRegistrationEventMetrics customerRegistrationEventMetrics;
  private final TransactionMonitoringClient transactionMonitoringClient;

  public void processCustomerRegistrationEvent(CustomerRegistration customerRegistration)
      throws TransactionMonitoringException {
    RegistrationDetails registrationDetails =
        CustomerRegistrationEventMapper.MAPPER.toRegistrationDetails(customerRegistration);
    try {
      log.info("CustomerRegistration Event partyKey {}", registrationDetails.getPartyKey());
      transactionMonitoringClient.sendCustomerRegistrationEvent(registrationDetails);
      customerRegistrationEventMetrics.incrementFAMCustomerRegistrationRequestsToTMASuccess();
    } catch (TransactionMonitoringException e) {
      customerRegistrationEventMetrics.incrementFAMCustomerRegistrationRequestsToTMAFailed();
      log.error(
          "Unable to send RegistrationDetails event with partyKey {} to transaction monitoring client",
          registrationDetails.getPartyKey(), e);
      throw e;
    }
  }

}
