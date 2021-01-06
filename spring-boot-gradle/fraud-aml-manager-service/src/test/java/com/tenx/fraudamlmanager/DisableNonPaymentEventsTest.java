package com.tenx.fraudamlmanager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsDetails;
import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsService;
import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsTMAEngine;
import com.tenx.fraudamlmanager.authentication.reset.domain.AuthResetDetails;
import com.tenx.fraudamlmanager.authentication.reset.domain.IdentityAccountResetEventService;
import com.tenx.fraudamlmanager.authentication.reset.domain.IdentityAccountResetTMAEngine;
import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpDetails;
import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpService;
import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpTMAEngine;
import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.PaymentsNotificationTMAEngine;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyEventService;
import com.tenx.fraudamlmanager.onboarding.individual.domain.IndividualPartyEventService;
import com.tenx.fraudamlmanager.onboarding.payee.domain.PayeesEventService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.fraudamlmanager.registration.infrastructure.CustomerRegistrationEventService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "SEND_NON_PAYMENT_EVENT=false")
public class DisableNonPaymentEventsTest extends SpringBootTestBase {

  private static final String PARTY_KEY = "123";

  @SpyBean
  LoginAttemptsService loginAttemptsService;

  @MockBean
  LoginAttemptsTMAEngine loginAttemptsTMAEngine;

  @SpyBean
  IdentityAccountResetEventService identityAccountResetEventService;

  @MockBean
  IdentityAccountResetTMAEngine identityAccountResetTMAEngine;

  @MockBean
  StepUpTMAEngine stepUpTMAEngine;

  @SpyBean
  StepUpService stepUpService;

  @SpyBean
  PaymentsNotificationTMAEngine paymentsNotificationTMAEngine;

  @SpyBean
  BusinessPartyEventService businessPartyEventService;

  @SpyBean
  IndividualPartyEventService individualPartyEventService;

  @SpyBean
  PayeesEventService payeesEventService;

  @SpyBean
  CustomerRegistrationEventService customerRegistrationEventService;

  @MockBean
  TransactionMonitoringClient transactionMonitoringClient;

  @Test
  public void testNotSendingLoginAttempts() throws TransactionMonitoringException {
    LoginAttemptsDetails loginAttemptsDetails = mock(LoginAttemptsDetails.class);
    when(loginAttemptsDetails.getPartyKey()).thenReturn(PARTY_KEY);
    when(loginAttemptsDetails.isValidTmaPayment()).thenReturn(true);
    loginAttemptsService.processLoginAttemptsEvent(loginAttemptsDetails);
    verify(loginAttemptsTMAEngine, times(0)).executeLoginAttempts(any());
    verify(transactionMonitoringClient, times(0)).sendLoginAttemptsEvent(any());
  }

  @Test
  public void testNotSendingIdentityAccountResetEvent() throws TransactionMonitoringException {
    AuthResetDetails authResetDetails = mock(AuthResetDetails.class);
    when(authResetDetails.getPartyKey()).thenReturn(PARTY_KEY);
    when(authResetDetails.isIdentityAccountResetApplicable()).thenReturn(true);
    identityAccountResetEventService.processIdentityAccountResetEvent(authResetDetails);
    verify(identityAccountResetTMAEngine, times(0)).executeAuthReset(any());
    verify(transactionMonitoringClient, times(0)).sendIdentityAccountResetEvent(any());
  }

  @Test
  public void testNotSendingStepUpEvents() throws TransactionMonitoringException {
    StepUpDetails stepUpDetails = mock(StepUpDetails.class);
    when(stepUpDetails.getPartyKey()).thenReturn(PARTY_KEY);
    when(stepUpDetails.isValidTmaPayment()).thenReturn(true);
    stepUpService.processStepUpEvent(stepUpDetails);
    verify(stepUpTMAEngine, times(0)).executeStepUp(any());
    verify(transactionMonitoringClient, times(0)).sendStepUpEvent(any());
  }

  @Test
  public void testNotSendingOnboardingEvents() throws TransactionMonitoringException {
    businessPartyEventService.processBusinessPartyEvent(any());
    verify(transactionMonitoringClient, times(0)).sendBusinessPartyEvent(any());
    individualPartyEventService.processIndividualPartyEvent(any(), any());
    verify(transactionMonitoringClient, times(0)).sendIndividualPartyEvent(any());
    payeesEventService.processPayeeCreateEvent(any());
    verify(transactionMonitoringClient, times(0)).sendPayeeEvent(any());
    payeesEventService.processPayeeDeleteEvent(any());
    verify(transactionMonitoringClient, times(0)).sendPayeeEvent(any());
    payeesEventService.processPayeeUpdateEvent(any());
    verify(transactionMonitoringClient, times(0)).sendPayeeEvent(any());
  }

  @Test
  public void testNotSendingMandatesEvents() throws TransactionMonitoringException {
    paymentsNotificationTMAEngine.executePaymentNotification(any());
    verify(transactionMonitoringClient, times(0)).sendMandatesEvent(any());
  }

  @Test
  public void testNotSendingRegistrationEvents() throws TransactionMonitoringException {
    customerRegistrationEventService.processCustomerRegistrationEvent(any());
    verify(transactionMonitoringClient, times(0)).sendCustomerRegistrationEvent(any());
  }

}
