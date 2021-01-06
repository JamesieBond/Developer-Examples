package com.tenx.fraudamlmanager.authentication.reset.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.authentication.reset.infrastructure.AuthReset;
import com.tenx.fraudamlmanager.authentication.reset.infrastructure.IdentityAccountReset;
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
public class AuthReSetServiceImplTest {

  @MockBean
  IdentityAccountResetTMAEngine identityAccountResetTMAEngine;

  private IdentityAccountResetEventService identityAccountResetEventService;

  @Captor
  private ArgumentCaptor<AuthReset> authResetArgumentCaptor;

  private static Stream<Arguments> payloadsSendtoTMA() {
    AuthResetDetails authResetDEtailPassed = new AuthResetDetails(
        "partyKey", "PASSED");

    AuthResetDetails authResetDetailFailed = new AuthResetDetails(
        "partyKey", "FAILED");

    AuthReset authResetPassed = new AuthReset(
        "partyKey", IdentityAccountReset.PASSED);

    AuthReset authResetFailed = new AuthReset(
        "partyKey", IdentityAccountReset.FAILED);

    return Stream.of(
        Arguments.of(authResetDEtailPassed, authResetPassed),
        Arguments.of(authResetDetailFailed, authResetFailed)
    );
  }

  private static Stream<Arguments> payloadsDontSendToTMA() {
    AuthResetDetails authResetAuthOutComeempty = new AuthResetDetails(
        "partyKey", "");

    AuthResetDetails authResetAuthOutComeNull = new AuthResetDetails(
        "partyKey", null);

    AuthResetDetails authResetAuthOutComeRandomString = new AuthResetDetails(
        "partyKey", "RandomString");

    return Stream.of(
        Arguments.of(authResetAuthOutComeempty),
        Arguments.of(authResetAuthOutComeNull),
        Arguments.of(authResetAuthOutComeRandomString)
    );
  }

  @BeforeEach
  public void beforeEach() {
    this.identityAccountResetEventService =
        new IdentityAccountResetEventServiceImpl(identityAccountResetTMAEngine);
  }

  @ParameterizedTest
  @MethodSource("payloadsSendtoTMA")
  public void checkAuthResetServiceCalltoLoginEngineTMA(AuthResetDetails input, AuthReset output)
      throws TransactionMonitoringException {

    Assertions.assertThatCode(() -> identityAccountResetEventService.processIdentityAccountResetEvent(input))
        .doesNotThrowAnyException();

      Mockito.verify(identityAccountResetTMAEngine, times(1))
          .executeAuthReset(authResetArgumentCaptor.capture());

    AuthReset capturedDetails = authResetArgumentCaptor.getValue();

    assertEquals(output.getPartyKey(), capturedDetails.getPartyKey());
    assertEquals(output.getResult(), capturedDetails.getResult());

  }

  @ParameterizedTest
  @MethodSource("payloadsDontSendToTMA")
  public void checkAuthResetServiceNOTCallLoginEngineTMA(AuthResetDetails input)
      throws TransactionMonitoringException {

      Assertions.assertThatCode(() -> identityAccountResetEventService.processIdentityAccountResetEvent(input))
        .doesNotThrowAnyException();

      Mockito.verify(identityAccountResetTMAEngine, times(0))
          .executeAuthReset(authResetArgumentCaptor.capture());
  }
}
