package com.tenx.fraudamlmanager.authentication.login.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tenx.fraudamlmanager.authentication.loginattempts.api.LoginAttemptsEventListener;
import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsDetails;
import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.Login;
import java.util.stream.Stream;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class LoginAttemptsEventListenerTest {

    private LoginAttemptsEventListener loginAttemptsEventListener;

    @Mock
    private Acknowledgment acknowledgment;

    @MockBean
    private LoginAttemptsService loginAttemptsService;

    @Captor
    private ArgumentCaptor<LoginAttemptsDetails> loginAttemptsCaptor;

    private static Stream<Arguments> payloads() {
        Login loginSuccess = Login.newBuilder().setAuthOutcome("SUCCESS")
            .setDeviceId("DEVICEID")
            .setFailureReason("FailureReason")
            .setLoginMethod("PASSCODE")
            .setPartyKey("PartyKey")
            .setTimestamp("timestamp")
            .build();

        Login loginFailed = Login.newBuilder().setAuthOutcome("FAILED")
            .setDeviceId("DEVICEID")
            .setFailureReason("FailureReason")
            .setLoginMethod("BIOMETRIC")
            .setPartyKey("PartyKey")
            .setTimestamp("timestamp")
            .build();

        Login loginPartyKeyNull = Login.newBuilder().setAuthOutcome("FAILED")
            .setDeviceId("DEVICEID")
            .setFailureReason("FailureReason")
            .setLoginMethod("BIOMETRIC")
            .setPartyKey("")
            .setTimestamp("timestamp")
            .build();

        LoginAttemptsDetails loginAttemptsDetailsSuccse = new LoginAttemptsDetails(
            "PartyKey", "SUCCESS", "PASSCODE", "FailureReason");

        LoginAttemptsDetails loginAttemptsDetailsFailure = new LoginAttemptsDetails(
            "PartyKey", "FAILED", "BIOMETRIC", "FailureReason");

        LoginAttemptsDetails loginAttemptsDetails = new LoginAttemptsDetails(
            "", "FAILED", "BIOMETRIC", "FailureReason");

        return Stream.of(
            Arguments.of(loginSuccess, loginAttemptsDetailsSuccse),
            Arguments.of(loginPartyKeyNull, loginAttemptsDetails),
            Arguments.of(loginFailed, loginAttemptsDetailsFailure)
        );
    }

    @BeforeEach
    public void initTest() {
        loginAttemptsEventListener = new LoginAttemptsEventListener(loginAttemptsService);
    }

    @ParameterizedTest
    @MethodSource("payloads")
    public void checkLoginAttemptsListener(Login input, LoginAttemptsDetails output)
        throws TransactionMonitoringException {

        ConsumerRecord<String, Login> consumerRecord =
            new ConsumerRecord<>("topic", 0, 0, "key", input);

        loginAttemptsEventListener.handleLoginAttemptsEvent(consumerRecord, acknowledgment);

        Mockito.verify(loginAttemptsService, VerificationModeFactory.times(1))
            .processLoginAttemptsEvent(loginAttemptsCaptor.capture());

        LoginAttemptsDetails capturedDetails = loginAttemptsCaptor.getValue();

        assertEquals(capturedDetails.getPartyKey(), output.getPartyKey());
        assertEquals(capturedDetails.getAuthOutcome(), output.getAuthOutcome());
        assertEquals(capturedDetails.getAuthMethod(), output.getAuthMethod());
        assertEquals(capturedDetails.getFailureReason(), output.getFailureReason());
    }
}