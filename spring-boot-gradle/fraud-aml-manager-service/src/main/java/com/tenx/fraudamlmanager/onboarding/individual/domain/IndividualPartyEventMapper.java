package com.tenx.fraudamlmanager.onboarding.individual.domain;

import com.tenx.fraudamlmanager.onboarding.individual.api.IndividualPartyDetails;
import com.tenxbanking.party.event.CustomerEventV3;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IndividualPartyEventMapper {

  IndividualPartyEventMapper MAPPER = Mappers.getMapper(IndividualPartyEventMapper.class);

  @Mapping(target = "addresses", source = "address")
  @Mapping(target = "updateType", ignore = true)
  @Mapping(target = "dateOfBirth", ignore = true)
  @Mapping(target = "currentAddress", ignore = true)
  IndividualPartyDetails toIndividualPartyDetails(CustomerEventV3 partyEvent);

}
