package com.tenx.fraudamlmanager.authentication.reset.infrastructure;

import com.tenx.fraudamlmanager.authentication.reset.domain.IdentityAccountResetTMAEngine;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityAccountResetTMAEngineImpl implements IdentityAccountResetTMAEngine {

  private final IdentityAccountResetEventMetrics identityAccountResetEventMetrics;
  private final TransactionMonitoringClient transactionMonitoringClient;

  public void executeAuthReset(AuthReset authReset)
      throws TransactionMonitoringException {
    try {
      transactionMonitoringClient.sendIdentityAccountResetEvent(authReset);
      identityAccountResetEventMetrics.incrementFAMIdAccountResetRequestsToTMASuccess();
      log.info("Sent AuthReset with partyKey {} to transaction monitoring client", authReset.getPartyKey());
    } catch (TransactionMonitoringException e) {
      identityAccountResetEventMetrics.incrementFAMIdAccountResetRequestsToTMAFailed();
      log.error("Unable to send AuthReset with partyKey {} to transaction monitoring client",
          authReset.getPartyKey(), e);

      throw e;
    }
  }
}
