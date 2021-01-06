package com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure;

import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsTMAEngine;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptsTMAEngineImpl implements LoginAttemptsTMAEngine {

  private final LoginAttemptsEventMetrics loginAttemptsEventMetrics;
  private final TransactionMonitoringClient transactionMonitoringClient;

  public void executeLoginAttempts(LoginAttempts loginAttempts)
      throws TransactionMonitoringException {
    try {
      transactionMonitoringClient.sendLoginAttemptsEvent(loginAttempts);
      loginAttemptsEventMetrics.incrementFAMLoginAttemptRequestsToTMASuccess();
      log.info("Sent LoginAttempts event with partyKey {} to transaction monitoring client",
          loginAttempts.getPartyKey());
    } catch (TransactionMonitoringException e) {
      loginAttemptsEventMetrics.incrementFAMLoginAttemptRequestsToTMAFailed();
      log.error("Unable to send  event with partyKey {} to transaction monitoring client",
          loginAttempts.getPartyKey(), e);
      throw e;
    }
  }
}
