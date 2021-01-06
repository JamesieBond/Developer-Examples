package com.tenx.fraudamlmanager.onboarding.payee.domain;

import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.payeemanager.event.payee.PayeesCreate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayeeCreateEventMapper {
    PayeeCreateEventMapper MAPPER = Mappers.getMapper(PayeeCreateEventMapper.class);

    @Mapping(target = "accountId", source = "payerPartyKey")
    @Mapping(target = "authenticationMethod", constant = "none")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "beneficiary", ignore = true)
    @Mapping(target = "changeType", ignore = true)
    PayeeData toPayee(PayeesCreate payeeEvent);
}
