package com.tenx.fraudamlmanager.onboarding.business.domain;

import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;

public interface BusinessPartyEventService {

    void processBusinessPartyEvent(BusinessPartyDetails businessPartyDetails) throws TransactionMonitoringException;

}
