package com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.PaymentsNotificationTMAEngine;
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
class PaymentsNotificationTMAEngineImplTest {

  @MockBean
  private PaymentNotificationEventMetrics paymentNotificationEventMetrics;

  @MockBean
  TransactionMonitoringClient transactionMonitoringClient;

  private PaymentsNotificationTMAEngine paymentsNotificationTMAEngine;

  @Captor
  private ArgumentCaptor<SetupMandates> loginAttemptsCaptor;

  @BeforeEach
  public void beforeEach() {
    this.paymentsNotificationTMAEngine =
        new PaymentsNotificationTMAEngineImpl(paymentNotificationEventMetrics,transactionMonitoringClient);
  }

  @Test
  void checkMandatesTMAEngineCallToTMA() throws TransactionMonitoringException {
    SetupMandates setupMandates = new SetupMandates(
        "partyKeyTest",
        "bacsDDMandateRef",
        "creditorAccountName",
        "directDebitKey",
        BeneficiaryAction.CANCELLATION);

    doNothing().when(transactionMonitoringClient).sendMandatesEvent(setupMandates);

    Assertions.assertThatCode(() -> paymentsNotificationTMAEngine.executePaymentNotification(setupMandates))
        .doesNotThrowAnyException();

    Mockito.verify(transactionMonitoringClient, times(1))
        .sendMandatesEvent(loginAttemptsCaptor.capture());
    Mockito.verify(paymentNotificationEventMetrics, VerificationModeFactory.times(1))
        .incrementFAMPaymentNotificationRequestsToTMASuccess();
  }

  @Test
  void checkMandatesTMAEngineCallToTMAFailure() throws TransactionMonitoringException {
    SetupMandates setupMandates = new SetupMandates(
        "partyKeyTest",
        "bacsDDMandateRef",
        "creditorAccountName",
        "directDebitKey",
        BeneficiaryAction.CANCELLATION);
    doThrow(TransactionMonitoringException.class).when(transactionMonitoringClient)
        .sendMandatesEvent(any(SetupMandates.class));
    assertThrows(
        TransactionMonitoringException.class, ()->
            paymentsNotificationTMAEngine.executePaymentNotification(setupMandates));
    verify(transactionMonitoringClient, times(1))
        .sendMandatesEvent(loginAttemptsCaptor.capture());
    verify(paymentNotificationEventMetrics, times(1))
        .incrementFAMPaymentNotificationRequestsToTMAFailed();
  }
}
