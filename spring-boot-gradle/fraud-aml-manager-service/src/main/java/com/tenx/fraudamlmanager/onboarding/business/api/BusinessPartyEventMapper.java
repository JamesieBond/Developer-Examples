package com.tenx.fraudamlmanager.onboarding.business.api;

import com.tenx.fraudamlmanager.onboarding.business.domain.BusinessPartyDetails;
import com.tenxbanking.party.event.business.BusinessEventV2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BusinessPartyEventMapper {
    BusinessPartyEventMapper MAPPER = Mappers.getMapper(BusinessPartyEventMapper.class);

    @Mapping(target = "companyName", source = "fullLegalName")
    @Mapping(target = "registrationNumber", source = "businessIdentificationCode")
    @Mapping(target = "updateType", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "registeredAddress", ignore = true)
    BusinessPartyDetails toBusinessPartyDetails(BusinessEventV2 businessEvent);
}
