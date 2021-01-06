package com.tenx.fraudamlmanager.authentication.reset.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.authentication.reset.domain.IdentityAccountResetTMAEngine;
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
class AuthResetTMAEngineImplTest {

  @MockBean
  private IdentityAccountResetEventMetrics identityAccountResetEventMetrics;

  @MockBean
  TransactionMonitoringClient transactionMonitoringClient;

  private IdentityAccountResetTMAEngine identityAccountResetTMAEngine;

  @Captor
  private ArgumentCaptor<AuthReset> authResetArgumentCaptor;

  @BeforeEach
  public void beforeEach() {
    this.identityAccountResetTMAEngine =
        new IdentityAccountResetTMAEngineImpl(identityAccountResetEventMetrics, transactionMonitoringClient);
  }

  @Test
  void checkStepUpTMAEngineCallToTMA() throws TransactionMonitoringException {
    AuthReset authReset = new AuthReset(
        "partyKey", IdentityAccountReset.FAILED);

    doNothing().when(transactionMonitoringClient).sendIdentityAccountResetEvent(authReset);

    Assertions.assertThatCode(() -> identityAccountResetTMAEngine.executeAuthReset(authReset))
        .doesNotThrowAnyException();

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendIdentityAccountResetEvent(authResetArgumentCaptor.capture());
    Mockito.verify(identityAccountResetEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMIdAccountResetRequestsToTMASuccess();
  }

  @Test
  void checkStepUpTMAEngineCallToTMAFailure() throws TransactionMonitoringException {
    AuthReset authReset = new AuthReset(
        "partyKey", IdentityAccountReset.FAILED);
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendIdentityAccountResetEvent(any(AuthReset.class));
    assertThrows(
        TransactionMonitoringException.class, () ->
            identityAccountResetTMAEngine.executeAuthReset(authReset));
    verify(transactionMonitoringClient, times(1)).sendIdentityAccountResetEvent(authResetArgumentCaptor.capture());
    verify(identityAccountResetEventMetrics, times(1)).incrementFAMIdAccountResetRequestsToTMAFailed();
  }
}
