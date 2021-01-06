package com.tenx.fraudamlmanager.authentication.loginattempts.api;

import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsDetails;
import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsService;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenx.security.forgerockfacade.resource.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginAttemptsEventListener {

    private final LoginAttemptsService loginAttemptsEventService;

    @KafkaListener(id = "LoginAttemptsEventListener", containerFactory = "deadLetterQueueKafkaListener", topics = "${spring.kafka.consumer.identity-login-v1-topic}", idIsGroup = false)
    public void handleLoginAttemptsEvent(
        ConsumerRecord<String, Login> loginEventCR,
        Acknowledgment acknowledgment) throws TransactionMonitoringException {
        LoginAttemptsDetails loginAttemptsDetails =
            LoginAttemptsEventMapper.MAPPER.mapToLoginInAttemptsDetails(loginEventCR.value());
        log.info("Login event with id {} received from listener", loginAttemptsDetails.getPartyKey());
        loginAttemptsEventService.processLoginAttemptsEvent(loginAttemptsDetails);
        acknowledgment.acknowledge();
    }
}
