package com.tenx.fraudamlmanager.onboarding.business.api;

import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyDetails;
import com.tenxbanking.party.event.business.BusinessEventV2;

public interface BusinessPartyEventMapperService {

    BusinessPartyDetails mapBusinessPartyDetails(BusinessEventV2 businessEventV2, String updateType);
}
