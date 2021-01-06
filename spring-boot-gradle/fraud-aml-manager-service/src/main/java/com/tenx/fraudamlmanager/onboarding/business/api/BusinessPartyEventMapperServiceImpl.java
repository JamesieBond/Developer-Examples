package com.tenx.fraudamlmanager.onboarding.business.api;

import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyDetails;
import com.tenxbanking.party.event.business.BusinessEventV2;
import org.springframework.stereotype.Service;

@Service
public class BusinessPartyEventMapperServiceImpl implements BusinessPartyEventMapperService {

    public BusinessPartyDetails mapBusinessPartyDetails(
            BusinessEventV2 businessEvent, String updateType) {
        BusinessPartyDetails businessPartyDetails =
                BusinessPartyEventMapper.MAPPER.toBusinessPartyDetails(businessEvent);
        businessPartyDetails.setUpdateType(updateType);
        return businessPartyDetails;
    }
}
