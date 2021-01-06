package com.tenx.fraudamlmanager.beneficiaries.mandates.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.PaymentsNotificationsService;
import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.SetupMandatesDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.payment.configuration.directdebit.event.v1.DirectDebitEvent;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PaymentsNotificationsEventListenerTest {


    private PaymentsNotificationsListener paymentsNotificationsListener;

    @Mock
    private Acknowledgment acknowledgment;

    @MockBean
    private PaymentsNotificationsService paymentsNotificationsService;

    @Captor
    private ArgumentCaptor<SetupMandatesDetails> mandatesBeneficiaryCaptor;

    private static Stream<Arguments> payloads() {
        DirectDebitEvent directDebitEventSetup = DirectDebitEvent.newBuilder()
            .setAction("SETUP")
            .setBacsDdMandateId("Test")
            .setBacsDDMandateRef("bacsDDMandateRef")
            .setCreditorAccountName("creditorAccountName")
            .setCreditorAccountNumber("Test")
            .setCreditorSortCode("Test")
            .setCurrency("Test")
            .setDateLastProcessed("Test")
            .setDateReceived("Test")
            .setDebitorAccountName("Test")
            .setDebitorAccountNumber("Test")
            .setDebitorSortCode("Test")
            .setDirectDebitKey("directDebitKey")
            .setIdempotencyKey("Test")
            .setPartyKey("partyKeyTest")
            .setProcessorMandateId("Test")
            .setTenantKey("Test")
            .build();

        DirectDebitEvent directDebitEventCancellation = DirectDebitEvent.newBuilder()
            .setAction("CANCELLATION")
            .setBacsDdMandateId("Test")
            .setBacsDDMandateRef("bacsDDMandateRef")
            .setCreditorAccountName("creditorAccountName")
            .setCreditorAccountNumber("Test")
            .setCreditorSortCode("Test")
            .setCurrency("Test")
            .setDateLastProcessed("Test")
            .setDateReceived("Test")
            .setDebitorAccountNumber("Test")
            .setDebitorAccountName("Test")
            .setDebitorSortCode("Test")
            .setDirectDebitKey("directDebitKey")
            .setIdempotencyKey("Test")
            .setPartyKey("partyKeyTest")
            .setProcessorMandateId("Test")
            .setTenantKey("Test")
            .build();

        SetupMandatesDetails mandatesBeneficiarySetup = new SetupMandatesDetails(
            "partyKeyTest",
            "bacsDDMandateRef",
            "creditorAccountName",
            "directDebitKey",
            "SETUP");

        SetupMandatesDetails mandatesBeneficiaryCancellation = new SetupMandatesDetails(
            "partyKeyTest",
            "bacsDDMandateRef",
            "creditorAccountName",
            "directDebitKey",
            "CANCELLATION");

        return Stream.of(
            Arguments.of(directDebitEventSetup, mandatesBeneficiarySetup),
            Arguments.of(directDebitEventCancellation, mandatesBeneficiaryCancellation)
        );
    }

    @BeforeEach
    public void initTest() {
        paymentsNotificationsListener = new PaymentsNotificationsListener(paymentsNotificationsService);
    }

    @ParameterizedTest
    @MethodSource("payloads")
    public void checkPaymentsNotificationsListener(DirectDebitEvent input, SetupMandatesDetails setupMandatesDetails)
        throws TransactionMonitoringException {
        ConsumerRecord<String, DirectDebitEvent> consumerRecord =
            new ConsumerRecord<>("topic", 0, 0, "key", input);

        doThrow(new NullPointerException("test")).when(paymentsNotificationsService)
            .processMandatesBeneficiary(any());
        assertThrows(
            NullPointerException.class, () ->
                paymentsNotificationsListener.handleDirectDebitEvent(consumerRecord, acknowledgment));

        ArgumentCaptor<SetupMandatesDetails> capturedDetails = mandatesBeneficiaryCaptor
            .forClass(SetupMandatesDetails.class);

        Mockito.verify(paymentsNotificationsService, times(1)).processMandatesBeneficiary(capturedDetails.capture());
        Mockito.verify(acknowledgment, times(0)).acknowledge();

        assertEquals(setupMandatesDetails.getPartyKey(), capturedDetails.getValue().getPartyKey());
        assertEquals(setupMandatesDetails.getReference(), capturedDetails.getValue().getReference());
        assertEquals(setupMandatesDetails.getAccountName(), capturedDetails.getValue().getAccountName());
        assertEquals(setupMandatesDetails.getDirectDebitKey(), capturedDetails.getValue().getDirectDebitKey());
        assertEquals(setupMandatesDetails.getAction(), capturedDetails.getValue().getAction());
    }
}
