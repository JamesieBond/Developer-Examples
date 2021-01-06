package com.tenx.fraudamlmanager.beneficiaries.mandates.domain;

import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.SetupMandates;
import com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure.SetupMandatesDetailsMapper;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsNotificationsEventServiceImpl implements PaymentsNotificationsService {


    private final PaymentsNotificationTMAEngine paymentsNotificationTMAEngine;

    public void processMandatesBeneficiary(SetupMandatesDetails setupMandatesDetails)
        throws TransactionMonitoringException {
        log.info("Filtering PaymentsNotification event partyKey {}", setupMandatesDetails.getPartyKey());

        if (setupMandatesDetails.isActionApplicable()) {
            SetupMandates setupMandates = SetupMandatesDetailsMapper.MAPPER.mapToSetupMandates(setupMandatesDetails);
            paymentsNotificationTMAEngine.executePaymentNotification(setupMandates);
        } else {
            log.info("PaymentsNotification event with partyKey {} not applicable", setupMandatesDetails.getPartyKey());
        }
    }
}
