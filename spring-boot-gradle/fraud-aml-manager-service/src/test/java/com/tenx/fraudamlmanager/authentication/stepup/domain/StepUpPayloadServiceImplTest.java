package com.tenx.fraudamlmanager.authentication.stepup.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpAuthMethod;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpAuthOutcome;
import com.tenx.fraudamlmanager.authentication.stepup.infrastructure.StepUpPayload;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class StepUpPayloadServiceImplTest {

  @MockBean
  StepUpTMAEngine stepUpTMAEngine;

  private StepUpEventServiceImpl stepUpEventService;

  @Captor
  private ArgumentCaptor<StepUpPayload> stepUpDetailsArgumentCaptorCaptor;

  private static Stream<Arguments> payloads() {
    StepUpDetails loginDetailsSuccessPasscode = new StepUpDetails(
        "partyKey", "STEPUP_SUCCESS",
        "PASSCODE", "failureReason");

    StepUpDetails loginDetailsFailedBiometric = new StepUpDetails(
        "partyKey", "STEPUP_FAILED",
        "BIOMETRIC", "failureReason");

    StepUpPayload loginSuccessPasscode = new StepUpPayload(
        "partyKey", StepUpAuthOutcome.STEPUP_SUCCESS,
        StepUpAuthMethod.PASSCODE, "failureReason");

    StepUpPayload loginFailedBiometric = new StepUpPayload(
        "partyKey", StepUpAuthOutcome.STEPUP_FAILED,
        StepUpAuthMethod.BIOMETRIC, "failureReason");

    return Stream.of(
        Arguments.of(loginDetailsSuccessPasscode, loginSuccessPasscode),
        Arguments.of(loginDetailsFailedBiometric, loginFailedBiometric)
    );
  }

  private static Stream<Arguments> payloadsDontSendToTMA() {
    StepUpDetails stepUpDetailsAuthOutComeempty = new StepUpDetails(
        "partyKey", "",
        "PASSCODE", "failureReason");

    StepUpDetails stepUpDetailsAuthOutComeNull = new StepUpDetails(
        "partyKey", null,
        "PASSCODE", "failureReason");

    StepUpDetails stepUpDetailsAuthOutComeRandomString = new StepUpDetails(
        "partyKey", "RandomString",
        "PASSCODE", "failureReason");

    StepUpDetails stepUpDetailsAuthMthodEmpty = new StepUpDetails(
        "partyKey", "STEPUP_FAILED",
        "", "failureReason");

    StepUpDetails stepUpDetailsAuthMthodNull = new StepUpDetails(
        "partyKey", "STEPUP_FAILED",
        null, "failureReason");

    StepUpDetails stepUpDetailsAuthMthodRandomString = new StepUpDetails(
        "partyKey", "STEPUP_FAILED",
        "RandomString", "failureReason");

    return Stream.of(
        Arguments.of(stepUpDetailsAuthOutComeempty),
        Arguments.of(stepUpDetailsAuthOutComeNull),
        Arguments.of(stepUpDetailsAuthOutComeRandomString),
        Arguments.of(stepUpDetailsAuthMthodEmpty),
        Arguments.of(stepUpDetailsAuthMthodNull),
        Arguments.of(stepUpDetailsAuthMthodRandomString)
    );
  }

  @BeforeEach
  public void beforeEach() {
    this.stepUpEventService =
        new StepUpEventServiceImpl(stepUpTMAEngine);
  }

  @ParameterizedTest
  @MethodSource("payloads")
  public void checkStepUpServiceCalltoStepUpTMAEngine(StepUpDetails input, StepUpPayload output)
      throws TransactionMonitoringException {

    Assertions.assertThatCode(() -> stepUpEventService.processStepUpEvent(input))
        .doesNotThrowAnyException();

      Mockito.verify(stepUpTMAEngine, times(1))
          .executeStepUp(stepUpDetailsArgumentCaptorCaptor.capture());

    StepUpPayload capturedDetails = stepUpDetailsArgumentCaptorCaptor.getValue();

    assertEquals(output.getPartyKey(), capturedDetails.getPartyKey());
    assertEquals(output.getAuthOutcome(), capturedDetails.getAuthOutcome());
    assertEquals(output.getAuthMethod(), capturedDetails.getAuthMethod());
    assertEquals(output.getFailureReason(), capturedDetails.getFailureReason());
  }

  @ParameterizedTest
  @MethodSource("payloadsDontSendToTMA")
  public void checkLoginAttemptsServiceNOTCallLoginEngineTMA(StepUpDetails input)
      throws TransactionMonitoringException {

      Assertions.assertThatCode(() -> stepUpEventService.processStepUpEvent(input))
        .doesNotThrowAnyException();

      Mockito.verify(stepUpTMAEngine, times(0))
          .executeStepUp(stepUpDetailsArgumentCaptorCaptor.capture());
  }
}
