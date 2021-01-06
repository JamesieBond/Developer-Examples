package com.tenx.fraudamlmanager.authentication.reset.api;


import com.tenx.fraudamlmanager.authentication.reset.domain.AuthResetDetails;
import com.tenx.security.forgerockfacade.resource.AccountResetNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IdentityAccountResetEventMapper {
    IdentityAccountResetEventMapper MAPPER = Mappers.getMapper(IdentityAccountResetEventMapper.class);

    @Mapping(target = "partyKey", source = "party_key")
    @Mapping(target = "result", source = "check_result")
    AuthResetDetails toIdentityAccountResetDetails(AccountResetNotification accountResetNotification);
}
