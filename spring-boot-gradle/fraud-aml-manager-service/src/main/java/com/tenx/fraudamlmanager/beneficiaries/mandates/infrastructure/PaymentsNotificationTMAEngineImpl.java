package com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure;

import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.PaymentsNotificationTMAEngine;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "SEND_NON_PAYMENT_EVENT", matchIfMissing = true)
public class PaymentsNotificationTMAEngineImpl implements PaymentsNotificationTMAEngine {

  private final PaymentNotificationEventMetrics paymentNotificationEventMetrics;
  private final TransactionMonitoringClient transactionMonitoringClient;

  public void executePaymentNotification(SetupMandates setupMandates)
      throws TransactionMonitoringException {
    try {
      transactionMonitoringClient.sendMandatesEvent(setupMandates);
      paymentNotificationEventMetrics.incrementFAMPaymentNotificationRequestsToTMASuccess();
      log.info("Sent PaymentsNotification event with partyKey {} to transaction monitoring client",
          setupMandates.getPartyKey());
    } catch (TransactionMonitoringException e) {
      paymentNotificationEventMetrics.incrementFAMPaymentNotificationRequestsToTMAFailed();
      log.error("Unable to send PaymentsNotification event with partyKey {} to transaction monitoring client",
          setupMandates.getPartyKey(),
          e);
      throw e;
    }
  }
}
