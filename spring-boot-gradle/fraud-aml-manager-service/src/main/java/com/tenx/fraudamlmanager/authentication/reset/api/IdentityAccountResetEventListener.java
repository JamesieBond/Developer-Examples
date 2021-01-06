package com.tenx.fraudamlmanager.authentication.reset.api;

import com.tenx.fraudamlmanager.authentication.reset.domain.AuthResetDetails;
import com.tenx.fraudamlmanager.authentication.reset.domain.IdentityAccountResetEventService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.AccountResetNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IdentityAccountResetEventListener {

    private final IdentityAccountResetEventService identityAccountResetEventService;

    @KafkaListener(id = "IdentityAccountResetEventListener", containerFactory = "deadLetterQueueKafkaListener",topics = "${spring.kafka.consumer.identity-account-reset-notification-topic}", idIsGroup = false)
    public void handleIdentityAccountResetEvent(
        ConsumerRecord<String, AccountResetNotification> identityAccountResetEventCR, Acknowledgment acknowledgment)
        throws TransactionMonitoringException {
        AuthResetDetails authResetDetails =
            IdentityAccountResetEventMapper.MAPPER.toIdentityAccountResetDetails(identityAccountResetEventCR.value());
        log.info("AccountResetNotification event with  id {} received from Listener.", authResetDetails.getPartyKey());
        identityAccountResetEventService.processIdentityAccountResetEvent(authResetDetails);
        acknowledgment.acknowledge();
    }
}
