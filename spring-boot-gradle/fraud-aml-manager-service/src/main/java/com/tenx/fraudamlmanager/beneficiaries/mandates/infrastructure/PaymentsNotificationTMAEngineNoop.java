package com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure;

import com.tenx.fraudamlmanager.beneficiaries.mandates.domain.PaymentsNotificationTMAEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(value = "SEND_NON_PAYMENT_EVENT", havingValue = "false")
public class PaymentsNotificationTMAEngineNoop implements PaymentsNotificationTMAEngine {

  @Override
  public void executePaymentNotification(SetupMandates setupMandates) {
    log.info("Mock called since TMA notifications of non payment events are disabled.");
  }
}
