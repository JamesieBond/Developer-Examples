package com.tenx.fraudamlmanager.authentication.login.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsTMAEngine;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.AuthMethod;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.AuthOutcome;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttempts;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttemptsEventMetrics;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttemptsTMAEngineImpl;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class LoginAttemptTMAEngineImplTest {

  @MockBean
  private LoginAttemptsEventMetrics tmaRequestForPlatformEventMetrics;

  @MockBean
  TransactionMonitoringClient transactionMonitoringClient;

  private LoginAttemptsTMAEngine loginAttemptsTMAEngine;

  @Captor
  private ArgumentCaptor<LoginAttempts> loginAttemptsCaptor;

  @BeforeEach
  public void beforeEach() {
    this.loginAttemptsTMAEngine =
        new LoginAttemptsTMAEngineImpl(tmaRequestForPlatformEventMetrics, transactionMonitoringClient);
  }

  @Test
  void checkStepUpTMAEngineCallToTMA() throws TransactionMonitoringException {
    LoginAttempts loginAttempts = new LoginAttempts(
        "partyKey", AuthOutcome.SUCCESS,
        AuthMethod.PASSCODE, "failureReason");

    doNothing().when(transactionMonitoringClient).sendLoginAttemptsEvent(loginAttempts);

    Assertions.assertThatCode(() -> loginAttemptsTMAEngine.executeLoginAttempts(loginAttempts))
        .doesNotThrowAnyException();

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendLoginAttemptsEvent(loginAttemptsCaptor.capture());
    Mockito.verify(tmaRequestForPlatformEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMLoginAttemptRequestsToTMASuccess();
  }

  @Test
  void checkStepUpTMAEngineCallToTMAFailure() throws TransactionMonitoringException {
    LoginAttempts loginAttempts = new LoginAttempts(
        "partyKey", AuthOutcome.SUCCESS,
        AuthMethod.PASSCODE, "failureReason");
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendLoginAttemptsEvent(any(LoginAttempts.class));
    assertThrows(
        TransactionMonitoringException.class, ()->
            loginAttemptsTMAEngine.executeLoginAttempts(loginAttempts));
    verify(transactionMonitoringClient, times(1))
        .sendLoginAttemptsEvent(loginAttemptsCaptor.capture());
    verify(tmaRequestForPlatformEventMetrics, times(1)).incrementFAMLoginAttemptRequestsToTMAFailed();
  }
}
