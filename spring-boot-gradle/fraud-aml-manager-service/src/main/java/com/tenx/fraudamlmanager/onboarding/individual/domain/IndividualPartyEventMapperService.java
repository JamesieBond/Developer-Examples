package com.tenx.fraudamlmanager.onboarding.individual.domain;

import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyDetails;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import com.tenxbanking.party.event.CustomerEventV3;

public interface IndividualPartyEventMapperService {

    IndividualPartyDetails mapIndividualPartyDetails(CustomerEventV3 customerEventV3,
                                                     String updateType) throws TransactionMonitoringException;

}
