package com.tenx.fraudamlmanager.authentication.reset.domain;

import com.tenx.fraudamlmanager.authentication.reset.infrastructure.AuthReset;
import com.tenx.fraudamlmanager.authentication.reset.infrastructure.IdentityAccountResetDetailsMapper;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "SEND_NON_PAYMENT_EVENT", matchIfMissing = true)
public class IdentityAccountResetEventServiceImpl implements IdentityAccountResetEventService {

    private final IdentityAccountResetTMAEngine identityAccountResetTMAEngine;

    public void processIdentityAccountResetEvent(AuthResetDetails authResetDetails)
        throws TransactionMonitoringException {
        log.info("Filtering AuthResetDetails partyKey {}", authResetDetails.getPartyKey());

        if (authResetDetails.isIdentityAccountResetApplicable()) {
            AuthReset authReset = IdentityAccountResetDetailsMapper.MAPPER.toAuthReset(authResetDetails);
            identityAccountResetTMAEngine.executeAuthReset(authReset);
        } else {
            log.info("AuthResetDetails with partyKey {} not applicable", authResetDetails.getPartyKey());
        }
    }
}
