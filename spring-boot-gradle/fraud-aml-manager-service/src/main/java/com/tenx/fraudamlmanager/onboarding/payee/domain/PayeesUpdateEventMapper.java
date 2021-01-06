package com.tenx.fraudamlmanager.onboarding.payee.domain;

import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.payeemanager.event.payee.PayeesUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayeesUpdateEventMapper {
    PayeesUpdateEventMapper MAPPER = Mappers.getMapper(PayeesUpdateEventMapper.class);

    @Mapping(target = "partyKey", source = "headerPartyKey")
    @Mapping(target = "authenticationMethod", constant = "none")
    @Mapping(target = "beneficiary", ignore = true)
    @Mapping(target = "changeType", ignore = true)
    @Mapping(target = "payeeAccounts", ignore = true)
    PayeeData toPayeeUpdate(PayeesUpdate payeesUpdate);
}
