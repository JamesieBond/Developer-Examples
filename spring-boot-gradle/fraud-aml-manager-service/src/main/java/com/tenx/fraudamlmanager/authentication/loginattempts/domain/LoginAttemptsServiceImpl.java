package com.tenx.fraudamlmanager.authentication.loginattempts.domain;

import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttempts;
import com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure.LoginAttemptsDetailsMapper;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "SEND_NON_PAYMENT_EVENT", matchIfMissing = true)
public class LoginAttemptsServiceImpl implements LoginAttemptsService {

    private final LoginAttemptsTMAEngine loginAttemptsTMAEngine;

    public void processLoginAttemptsEvent(LoginAttemptsDetails loginAttemptsDetails)
        throws TransactionMonitoringException {
        log.info("Filtering LoginAttemptsDetails event partyKey {}", loginAttemptsDetails.getPartyKey());

        if (loginAttemptsDetails.isValidTmaPayment()) {
            LoginAttempts loginAttempts = LoginAttemptsDetailsMapper.MAPPER.mapToLoginInAttempts(loginAttemptsDetails);
            loginAttemptsTMAEngine.executeLoginAttempts(loginAttempts);
        } else {
            log.info("LoginAttemptsDetails event with partyKey {} not applicable", loginAttemptsDetails.getPartyKey());
        }
    }
}
