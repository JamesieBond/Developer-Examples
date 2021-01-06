package com.tenx.fraudamlmanager.authentication.login.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsDetails;
import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsServiceImpl;
import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsTMAEngine;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.AuthMethod;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.AuthOutcome;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttempts;
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
public class LoginAttemptServiceImplTest {

  @MockBean
  LoginAttemptsTMAEngine loginAttemptsTMAEngine;

  private LoginAttemptsServiceImpl loginAttemptsEventService;

  @Captor
  private ArgumentCaptor<LoginAttempts> loginAttemptsCaptor;

  private static Stream<Arguments> payloadsSendtoTMA() {
    LoginAttemptsDetails loginDetailsSuccessPasscode = new LoginAttemptsDetails(
        "partyKey", "SUCCESS",
        "PASSCODE", "failureReason");

    LoginAttemptsDetails loginDetailsFailedBiometric = new LoginAttemptsDetails(
        "partyKey", "FAILED",
        "BIOMETRIC", "failureReason");

    LoginAttempts loginSuccessPasscode = new LoginAttempts(
        "partyKey", AuthOutcome.SUCCESS,
        AuthMethod.PASSCODE, "failureReason");

    LoginAttempts loginFailedBiometric = new LoginAttempts(
        "partyKey", AuthOutcome.FAILED,
        AuthMethod.BIOMETRIC, "failureReason");

    return Stream.of(
        Arguments.of(loginDetailsSuccessPasscode, loginSuccessPasscode),
        Arguments.of(loginDetailsFailedBiometric, loginFailedBiometric)
    );
  }

  private static Stream<Arguments> payloadsDontSendToTMA() {
    LoginAttemptsDetails loginDetailsAuthOutComeempty = new LoginAttemptsDetails(
        "partyKey", "",
        "PASSCODE", "failureReason");

    LoginAttemptsDetails loginDetailsAuthOutComeNull = new LoginAttemptsDetails(
        "partyKey", null,
        "PASSCODE", "failureReason");

    LoginAttemptsDetails loginDetailsAuthOutComeRandomString = new LoginAttemptsDetails(
        "partyKey", "RandomString",
        "PASSCODE", "failureReason");

    LoginAttemptsDetails loginDetailsAuthMthodEmpty = new LoginAttemptsDetails(
        "partyKey", "FAILED",
        "", "failureReason");

    LoginAttemptsDetails loginDetailsAuthMthodNull = new LoginAttemptsDetails(
        "partyKey", "FAILED",
        null, "failureReason");

    LoginAttemptsDetails loginDetailsAuthMthodRandomString = new LoginAttemptsDetails(
        "partyKey", "FAILED",
        "RandomString", "failureReason");

    return Stream.of(
        Arguments.of(loginDetailsAuthOutComeempty),
        Arguments.of(loginDetailsAuthOutComeNull),
        Arguments.of(loginDetailsAuthOutComeRandomString),
        Arguments.of(loginDetailsAuthMthodEmpty),
        Arguments.of(loginDetailsAuthMthodNull),
        Arguments.of(loginDetailsAuthMthodRandomString)

    );
  }

  @BeforeEach
  public void beforeEach() {
    this.loginAttemptsEventService =
        new LoginAttemptsServiceImpl(loginAttemptsTMAEngine);
  }

  @ParameterizedTest
  @MethodSource("payloadsSendtoTMA")
  public void checkLoginAttemptsServiceCalltoLoginEngineTMA(LoginAttemptsDetails input, LoginAttempts output)
      throws TransactionMonitoringException {

    Assertions.assertThatCode(() -> loginAttemptsEventService.processLoginAttemptsEvent(input))
        .doesNotThrowAnyException();

      Mockito.verify(loginAttemptsTMAEngine, times(1))
          .executeLoginAttempts(loginAttemptsCaptor.capture());

    LoginAttempts capturedDetails = loginAttemptsCaptor.getValue();

    assertEquals(output.getPartyKey(), capturedDetails.getPartyKey());
    assertEquals(output.getAuthOutcome(), capturedDetails.getAuthOutcome());
    assertEquals(output.getAuthMethod(), capturedDetails.getAuthMethod());
    assertEquals(output.getFailureReason(), capturedDetails.getFailureReason());
  }

  @ParameterizedTest
  @MethodSource("payloadsDontSendToTMA")
  public void checkLoginAttemptsServiceNOTCallLoginEngineTMA(LoginAttemptsDetails input)
      throws TransactionMonitoringException {

      Assertions.assertThatCode(() -> loginAttemptsEventService.processLoginAttemptsEvent(input))
        .doesNotThrowAnyException();

      Mockito.verify(loginAttemptsTMAEngine, times(0))
          .executeLoginAttempts(loginAttemptsCaptor.capture());
  }
}
