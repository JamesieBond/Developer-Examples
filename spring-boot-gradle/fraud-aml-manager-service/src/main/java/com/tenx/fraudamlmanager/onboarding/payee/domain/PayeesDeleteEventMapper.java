package com.tenx.fraudamlmanager.onboarding.payee.domain;

import com.tenx.fraudamlmanager.onboarding.payee.api.PayeeData;
import com.tenx.payeemanager.event.payee.PayeesDelete;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayeesDeleteEventMapper {
    PayeesDeleteEventMapper MAPPER = Mappers.getMapper(PayeesDeleteEventMapper.class);

    @Mapping(target = "accountId", source = "payerPartyKey")
    @Mapping(target = "authenticationMethod", constant = "none")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "beneficiary", ignore = true)
    @Mapping(target = "changeType", ignore = true)
    PayeeData toPayeeDelete(PayeesDelete payeesDelete);
}
