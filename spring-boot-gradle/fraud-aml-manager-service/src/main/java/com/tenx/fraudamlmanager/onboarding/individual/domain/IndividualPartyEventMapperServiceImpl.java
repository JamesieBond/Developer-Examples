package com.tenx.fraudamlmanager.onboarding.individual.domain;

import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyDetails;
import com.tenxbanking.party.event.CustomerEventV3;
import org.springframework.stereotype.Service;

@Service
public class IndividualPartyEventMapperServiceImpl implements IndividualPartyEventMapperService {

  public IndividualPartyDetails mapIndividualPartyDetails(CustomerEventV3 customerEventV3,
                                                          String updateType) {
    IndividualPartyDetails individualPartyDetails =
            IndividualPartyEventMapper.MAPPER.toIndividualPartyDetails(customerEventV3);
    individualPartyDetails.setUpdateType(updateType);
    return individualPartyDetails;
  }
}
