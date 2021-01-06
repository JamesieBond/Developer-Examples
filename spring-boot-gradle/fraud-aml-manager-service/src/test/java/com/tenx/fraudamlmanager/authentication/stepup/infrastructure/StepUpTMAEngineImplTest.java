package com.tenx.fraudamlmanager.authentication.stepup.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpTMAEngine;
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
class StepUpTMAEngineImplTest {

  @MockBean
  private StepUpEventMetrics StepUpEventMetrics;

  @MockBean
  TransactionMonitoringClient transactionMonitoringClient;

  private StepUpTMAEngine stepUpTMAEngine;

  @Captor
  private ArgumentCaptor<StepUpPayload> stepUpPayloadArgumentCaptor;

  @BeforeEach
  public void beforeEach() {
    this.stepUpTMAEngine =
        new StepUpTMAEngineImpl(StepUpEventMetrics, transactionMonitoringClient);
  }

  @Test
  void checkLoginAttemptsServiceCallToTMA() throws TransactionMonitoringException {
    StepUpPayload stepUpPayload = new StepUpPayload(
        "partyKey", StepUpAuthOutcome.STEPUP_SUCCESS,
        StepUpAuthMethod.PASSCODE, "failureReason");

    doNothing().when(transactionMonitoringClient).sendStepUpEvent(stepUpPayload);

    Assertions.assertThatCode(() -> stepUpTMAEngine.executeStepUp(stepUpPayload))
        .doesNotThrowAnyException();

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendStepUpEvent(stepUpPayloadArgumentCaptor.capture());
    Mockito.verify(StepUpEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMStepUpRequestsToTMASuccess();
  }

  @Test
  void checkLoginAttemptsServiceCallToTMAFailure() throws TransactionMonitoringException {
    StepUpPayload stepUpPayload = new StepUpPayload(
        "partyKey", StepUpAuthOutcome.STEPUP_SUCCESS,
        StepUpAuthMethod.PASSCODE, "failureReason");
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendStepUpEvent(any(StepUpPayload.class));
    assertThrows(
        TransactionMonitoringException.class, () ->
            stepUpTMAEngine.executeStepUp(stepUpPayload));
    verify(transactionMonitoringClient, times(1)).sendStepUpEvent(stepUpPayloadArgumentCaptor.capture());
    verify(StepUpEventMetrics, times(1)).incrementFAMStepUpRequestsToTMAFailed();
  }
}
