package com.tenx.fraudamlmanager.onboarding.individual.domain;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.CustomerEventV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "SEND_NON_PAYMENT_EVENT", matchIfMissing = true)
public class IndividualPartyEventServiceImpl implements IndividualPartyEventService {

  private final IndividualPartyEventMetrics individualPartyEventMetrics;

  private final TransactionMonitoringClient transactionMonitoringClient;

  private final IndividualPartyEventMapperService individualPartyEventMapperService;


  public void processIndividualPartyEvent(CustomerEventV3 customerEventV3, String updateType)
      throws TransactionMonitoringException {
    IndividualPartyDetails individualPartyDetails =
        individualPartyEventMapperService.mapIndividualPartyDetails(customerEventV3, updateType);
    log.debug("IndividualPartyDetails event partyKey {}", individualPartyDetails.getPartyKey());
    try {
      if (individualPartyDetails.getCurrentAddress() != null) {
        log.debug("Sent IndividualPartyDetails event to TMA for partyKey {}",
            individualPartyDetails.getPartyKey());
        transactionMonitoringClient.sendIndividualPartyEvent(individualPartyDetails);
        individualPartyEventMetrics.incrementFAMIndividualPartyRequestsToTMASuccess();
      }
    } catch (TransactionMonitoringException e) {
      individualPartyEventMetrics.incrementFAMIndividualPartyRequestsToTMAFailed();
      log.error(
          "Unable to send IndividualPartyDetails event with partyKey {} to transaction monitoring client",
          individualPartyDetails.getPartyKey(), e);
      throw e;
    }
  }
}
