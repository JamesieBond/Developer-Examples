package com.tenx.fraudamlmanager.onboarding.business.domain;

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
public class BusinessPartyEventServiceImpl implements BusinessPartyEventService {

  private final BusinessPartyEventMetrics businessPartyEventMetrics;
  private final TransactionMonitoringClient transactionMonitoringClient;

  @Override
  public void processBusinessPartyEvent(BusinessPartyDetails businessPartyDetails)
      throws TransactionMonitoringException {
    log.debug("BusinessPartyDetails event partyKey {}", businessPartyDetails.getPartyKey());

    try {
      // For new there are only those 2 event. TMA will not be called if there is a new event created
      if (businessPartyDetails.isEventTypeCustomerProvisioned() || (businessPartyDetails.isEventTypeCustomerModified()
          && businessPartyDetails.isPartyStatusProvisioned())) {
        transactionMonitoringClient.sendBusinessPartyEvent(businessPartyDetails);
        businessPartyEventMetrics.incrementFAMBusinessPartyRequestsToTMASuccess();
      }
    } catch (TransactionMonitoringException e) {
      businessPartyEventMetrics.incrementFAMBusinessPartyRequestsToTMAFailed();
      log.error("Unable to send BusinessPartyDetails event with partyKey {} to transaction monitoring client",
          businessPartyDetails.getPartyKey(), e);
      throw e;
    }
  }
}