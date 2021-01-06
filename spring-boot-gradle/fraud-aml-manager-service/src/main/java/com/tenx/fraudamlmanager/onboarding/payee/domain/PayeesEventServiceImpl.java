package com.tenx.fraudamlmanager.onboarding.payee.domain;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.TransactionMonitoringClient;
import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.payeemanager.event.payee.PayeesCreate;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import com.tenx.payeemanager.event.payee.PayeesUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "SEND_NON_PAYMENT_EVENT", matchIfMissing = true)
public class PayeesEventServiceImpl implements PayeesEventService {

  private final PayeesEventMetrics payeesEventMetrics;

  private final TransactionMonitoringClient transactionMonitoringClient;

  private final PayeeEventMapperServiceImpl payeeCreateEventMapperService;

  @Override
  public void processPayeeCreateEvent(PayeesCreate payeesCreate) throws TransactionMonitoringException {
    PayeeData payeeData = payeeCreateEventMapperService.mapPayeeCreateDetails(payeesCreate);
    try {
      log.debug("PayeeCreate event partyKey {}", payeesCreate.getPartyKey());
      transactionMonitoringClient.sendPayeeEvent(payeeData);
      payeesEventMetrics.incrementFAMPayeesRequestsToTMASuccess();
    } catch (TransactionMonitoringException e) {
      payeesEventMetrics.incrementFAMPayeesRequestsToTMAFailed();
      log.error(
          "Unable to send PayeeData event with partyKey {} to transaction monitoring client",
          payeeData.getPartyKey(), e);
      throw e;
    }
  }

  @Override
  public void processPayeeUpdateEvent(PayeesUpdate payeesUpdate) throws TransactionMonitoringException {
    PayeeData payeeUpdateData = payeeCreateEventMapperService.mapPayeeUpdateDetails(payeesUpdate);
    try {
      log.debug("PayeeUpdate event partyKey {}", payeesUpdate.getHeaderPartyKey());
      transactionMonitoringClient.sendPayeeEvent(payeeUpdateData);
      payeesEventMetrics.incrementFAMPayeesRequestsToTMASuccess();
    } catch (TransactionMonitoringException e) {
      payeesEventMetrics.incrementFAMPayeesRequestsToTMAFailed();
      log.error(
          "Unable to send PayeeUpdateData event with partyKey {} to transaction monitoring client",
          payeeUpdateData.getPartyKey(), e);
      throw e;
    }
  }

  @Override
  public void processPayeeDeleteEvent(PayeesDelete payeesDelete) throws TransactionMonitoringException {
    PayeeData payeeDeleteData = payeeCreateEventMapperService.mapPayeeDeleteDetails(payeesDelete);
    try {
      log.debug("PayeeDelete event partyKey {}", payeesDelete.getPartyKey());
      transactionMonitoringClient.sendPayeeEvent(payeeDeleteData);
      payeesEventMetrics.incrementFAMPayeesRequestsToTMASuccess();
    } catch (TransactionMonitoringException e) {
      payeesEventMetrics.incrementFAMPayeesRequestsToTMAFailed();
      log.error(
          "Unable to send PayeeDeleteData event with partyKey {} to transaction monitoring client",
          payeeDeleteData.getPartyKey(), e);
      throw e;
    }
  }
}
