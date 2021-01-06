package com.tenx.fraudamlmanager.authentication.stepup.api;

import static org.junit.Assert.assertEquals;

import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpDetails;
import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.StepUp;
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
public class StepUpEventListenerTest {

    private StepUpEventListener stepUpEventListener;

    @Mock
    private Acknowledgment acknowledgment;

    @MockBean
    private StepUpService stepUpService;

    @Captor
    private ArgumentCaptor<StepUpDetails> stepUpDetailsArgumentCaptor;

    private static Stream<Arguments> payloads() {
        StepUp stepUpSuccess = StepUp
            .newBuilder()
            .setAuthOutcome("STEPUP_SUCCESS")
            .setDeviceId("DEVICEID")
            .setTransactionFailureReason("FailureReason")
            .setAuthMethod("BIOMETRIC")
            .setPartyKey("PartyKey")
            .setTimestamp("timestamp")
            .build();

        StepUp stepUpFailed = StepUp
            .newBuilder()
            .setAuthOutcome("STEPUP_FAILED")
            .setDeviceId("DEVICEID")
            .setTransactionFailureReason("FailureReason")
            .setAuthMethod("PASSCODE")
            .setPartyKey("PartyKey")
            .setTimestamp("timestamp")
            .build();

        StepUp stepUpPkNull = StepUp
            .newBuilder()
            .setAuthOutcome("STEPUP_FAILED")
            .setDeviceId("DEVICEID")
            .setTransactionFailureReason("FailureReason")
            .setAuthMethod("PASSCODE")
            .setPartyKey("")
            .setTimestamp("timestamp")
            .build();

        StepUpDetails stepUpDetailsSucces = new StepUpDetails(
            "PartyKey", "STEPUP_SUCCESS", "BIOMETRIC", "FailureReason");

        StepUpDetails stepUpDetailsFailed = new StepUpDetails(
            "PartyKey", "STEPUP_FAILED", "PASSCODE", "FailureReason");

        StepUpDetails stepUpDetailsPKnull = new StepUpDetails(
            "", "STEPUP_FAILED", "PASSCODE", "FailureReason");

        return Stream.of(
            Arguments.of(stepUpSuccess, stepUpDetailsSucces),
            Arguments.of(stepUpFailed, stepUpDetailsFailed),
            Arguments.of(stepUpPkNull, stepUpDetailsPKnull)
        );
    }

    @BeforeEach
    public void initTest() {
        stepUpEventListener = new StepUpEventListener(stepUpService);
    }

    @ParameterizedTest
    @MethodSource("payloads")
    public void checkStepUpListener(StepUp input, StepUpDetails output)
        throws TransactionMonitoringException {

        ConsumerRecord<String, com.tenx.security.forgerockfacade.resource.StepUp> consumerRecord =
            new ConsumerRecord<>("topic", 0, 0, "key", input);

        stepUpEventListener.handleStepUpEvent(consumerRecord, acknowledgment);

        Mockito.verify(stepUpService, VerificationModeFactory.times(1))
            .processStepUpEvent(stepUpDetailsArgumentCaptor.capture());

        StepUpDetails capturedDetails = stepUpDetailsArgumentCaptor.getValue();

        assertEquals(output.getPartyKey(), capturedDetails.getPartyKey());
        assertEquals(output.getAuthOutcome(), capturedDetails.getAuthOutcome());
        assertEquals(output.getAuthMethod(), capturedDetails.getAuthMethod());
        assertEquals(output.getFailureReason(), capturedDetails.getFailureReason());
    }
}