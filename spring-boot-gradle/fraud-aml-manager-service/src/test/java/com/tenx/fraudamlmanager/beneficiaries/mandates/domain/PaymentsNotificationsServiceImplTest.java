package com.tenx.fraudamlmanager.beneficiaries.mandates.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.BeneficiaryAction;
import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.SetupMandates;
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
public class PaymentsNotificationsServiceImplTest {

    @MockBean
    PaymentsNotificationTMAEngine paymentsNotificationTMAEngine;

    private PaymentsNotificationsEventServiceImpl paymentsNotificationsService;

    @Captor
    private ArgumentCaptor<SetupMandates> mandatesBeneficaryArgumentsCaptor;

    private static Stream<Arguments> payloadsSendtoTMA() {
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

        SetupMandates setupMandatesSetUp = new SetupMandates(
            "partyKeyTest",
            "bacsDDMandateRef",
            "creditorAccountName",
            "directDebitKey",
            BeneficiaryAction.SETUP);

        SetupMandates setupMandatesCancellation = new SetupMandates(
            "partyKeyTest",
            "bacsDDMandateRef",
            "creditorAccountName",
            "directDebitKey",
            BeneficiaryAction.CANCELLATION);

        return Stream.of(
            Arguments.of(mandatesBeneficiarySetup, setupMandatesSetUp),
            Arguments.of(mandatesBeneficiaryCancellation, setupMandatesCancellation)
        );
    }

    private static Stream<Arguments> payloadsDontSendToTMA() {

        SetupMandatesDetails mandatesBeneficiaryNull = new SetupMandatesDetails(
            "partyKeyTest",
            "bacsDDMandateRef",
            "creditorAccountName",
            "directDebitKey",
            "");

        SetupMandatesDetails mandatesBeneficiaryRandomString = new SetupMandatesDetails(
            "partyKeyTest",
            "bacsDDMandateRef",
            "creditorAccountName",
            "directDebitKey",
            "RANDOMESTRING");

        SetupMandatesDetails mandatesBeneficiaryEmpty = new SetupMandatesDetails(
            "partyKeyTest",
            "bacsDDMandateRef",
            "creditorAccountName",
            "directDebitKey",
            null);

        return Stream.of(
            Arguments.of(mandatesBeneficiaryNull),
            Arguments.of(mandatesBeneficiaryRandomString),
            Arguments.of(mandatesBeneficiaryEmpty)
        );
    }

    @BeforeEach
    public void beforeEach() {
        this.paymentsNotificationsService =
            new PaymentsNotificationsEventServiceImpl(paymentsNotificationTMAEngine);
    }

    @ParameterizedTest
    @MethodSource("payloadsSendtoTMA")
    public void checkPaymentsNotificationsServiceCallToPAymentNoticationTMAEngine(SetupMandatesDetails input,
        SetupMandates output) throws TransactionMonitoringException {

        Assertions.assertThatCode(() -> paymentsNotificationsService.processMandatesBeneficiary(input))
            .doesNotThrowAnyException();

        Mockito.verify(paymentsNotificationTMAEngine, times(1))
            .executePaymentNotification(mandatesBeneficaryArgumentsCaptor.capture());

        SetupMandates capturedDetails = mandatesBeneficaryArgumentsCaptor.getValue();

        assertEquals(output.getPartyKey(), capturedDetails.getPartyKey());
        assertEquals(output.getReference(), capturedDetails.getReference());
        assertEquals(output.getAccountName(), capturedDetails.getAccountName());
        assertEquals(output.getAction(), capturedDetails.getAction());
        assertEquals(output.getDirectDebitKey(), capturedDetails.getDirectDebitKey());
    }

    @ParameterizedTest
    @MethodSource("payloadsDontSendToTMA")
    public void checkLoginAttemptsServiceNOTCallLoginEngineTMA(SetupMandatesDetails input)
        throws TransactionMonitoringException {

        Assertions.assertThatCode(() -> paymentsNotificationsService.processMandatesBeneficiary(input))
            .doesNotThrowAnyException();

        Mockito.verify(paymentsNotificationTMAEngine, times(0))
            .executePaymentNotification(mandatesBeneficaryArgumentsCaptor.capture());
    }
}
