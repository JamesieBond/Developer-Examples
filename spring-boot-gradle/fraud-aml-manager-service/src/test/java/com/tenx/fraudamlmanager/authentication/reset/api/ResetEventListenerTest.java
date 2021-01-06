package com.tenx.fraudamlmanager.authentication.reset.api;

import static org.junit.Assert.assertEquals;

import com.tenx.fraudamlmanager.authentication.reset.domain.AuthResetDetails;
import com.tenx.fraudamlmanager.authentication.reset.domain.IdentityAccountResetEventService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.AccountResetNotification;
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
public class ResetEventListenerTest {

    private IdentityAccountResetEventListener identityAccountResetEventListener;

    @Mock
    private Acknowledgment acknowledgment;

    @MockBean
    private IdentityAccountResetEventService identityAccountResetEventService;

    @Captor
    private ArgumentCaptor<AuthResetDetails> authResetDetailsArgumentCaptor;

    private static Stream<Arguments> payloads() {
        AccountResetNotification accountResetNotificationSuccess = AccountResetNotification.newBuilder()
            .setPartyKey("PARTYKEY")
            .setCheckResult("PASSED")
            .setTenantKey("10000")
            .setTimestamp("Timestamp")
            .setTransactionId("TransactionId")
            .build();

        AccountResetNotification accountResetNotificationFailed = AccountResetNotification.newBuilder()
            .setPartyKey("PARTYKEY")
            .setCheckResult("FAILED")
            .setTenantKey("10000")
            .setTimestamp("Timestamp")
            .setTransactionId("TransactionId")
            .build();

        AuthResetDetails authResetDetailsSuccess = new AuthResetDetails("PARTYKEY", "PASSED");
        AuthResetDetails authResetDetialsFailure = new AuthResetDetails("PARTYKEY", "FAILED");

        return Stream.of(
            Arguments.of(accountResetNotificationSuccess, authResetDetailsSuccess),
            Arguments.of(accountResetNotificationFailed, authResetDetialsFailure)
        );
    }

    @BeforeEach
    public void initTest() {
        identityAccountResetEventListener = new IdentityAccountResetEventListener(identityAccountResetEventService);
    }

    @ParameterizedTest
    @MethodSource("payloads")
    public void checkAuthCheckListener(AccountResetNotification input, AuthResetDetails output)
        throws TransactionMonitoringException {

        ConsumerRecord<String, AccountResetNotification> consumerRecord =
            new ConsumerRecord<>("topic", 0, 0, "key", input);

        identityAccountResetEventListener.handleIdentityAccountResetEvent(consumerRecord, acknowledgment);

        Mockito.verify(identityAccountResetEventService, VerificationModeFactory.times(1))
            .processIdentityAccountResetEvent(authResetDetailsArgumentCaptor.capture());

        AuthResetDetails capturedDetails = authResetDetailsArgumentCaptor.getValue();

        assertEquals(output.getPartyKey(), capturedDetails.getPartyKey());
        assertEquals(output.getResult(), capturedDetails.getResult());

    }
}