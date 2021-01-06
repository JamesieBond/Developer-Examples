package com.tenx.fraudamlmanager.registration.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.CustomerRegistration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CustomerRegistrationEventServiceImplTest {

  @MockBean
  private CustomerRegistrationEventMetrics customerRegistrationEventMetrics;

  @MockBean
  private TransactionMonitoringClient transactionMonitoringClient;
  private CustomerRegistrationEventServiceImpl customerRegistrationEventSevice;

  @BeforeEach
  public void beforeEach() {
    this.customerRegistrationEventSevice =
        new CustomerRegistrationEventServiceImpl(customerRegistrationEventMetrics, transactionMonitoringClient);
  }

  /**
   * @throws Exception Generic exception
   */
  @Test
  void checkCustomerRegistrationEventService() throws TransactionMonitoringException {

    CustomerRegistration customerRegistration = createCustomerRegistrationEvent();
    ArgumentCaptor<RegistrationDetails> captor = ArgumentCaptor.forClass(RegistrationDetails.class);
    doNothing().when(transactionMonitoringClient).sendCustomerRegistrationEvent(captor.capture());
    customerRegistrationEventSevice.processCustomerRegistrationEvent(customerRegistration);
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendCustomerRegistrationEvent(captor.getValue());
    Mockito.verify(customerRegistrationEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMCustomerRegistrationRequestsToTMASuccess();
  }

  @Test
  void checkCustomerRegistrationEventServiceFailure() throws TransactionMonitoringException {

    CustomerRegistration customerRegistration = createCustomerRegistrationEvent();
    ArgumentCaptor<RegistrationDetails> captor = ArgumentCaptor.forClass(RegistrationDetails.class);
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendCustomerRegistrationEvent(captor.capture());
    assertThrows(
        TransactionMonitoringException.class, ()->
            customerRegistrationEventSevice.processCustomerRegistrationEvent(customerRegistration));
    Mockito.verify(transactionMonitoringClient, times(1))
        .sendCustomerRegistrationEvent(captor.getValue());
    verify(customerRegistrationEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMCustomerRegistrationRequestsToTMAFailed();
  }

  private CustomerRegistration createCustomerRegistrationEvent() {

    CustomerRegistration customerRegistrationEvent = CustomerRegistration.newBuilder()
        .setPartyKey("partyKey")
        .setPasscodeProvided(true)
        .setPasswordProvided(true)
        .setDeviceId("deviceId")
        .setTimestamp(ZonedDateTime.now().toString())
        .build();

    return customerRegistrationEvent;
  }
}
