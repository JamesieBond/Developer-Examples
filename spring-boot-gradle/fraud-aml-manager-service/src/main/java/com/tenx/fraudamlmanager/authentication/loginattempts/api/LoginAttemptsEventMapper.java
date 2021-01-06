package com.tenx.fraudamlmanager.authentication.loginattempts.api;

import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsDetails;
import com.tenx.security.forgerockfacade.resource.Login;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoginAttemptsEventMapper {
    LoginAttemptsEventMapper MAPPER = Mappers.getMapper(LoginAttemptsEventMapper.class);

    @Mapping(target = "partyKey", source = "party_key")
    @Mapping(target = "authOutcome", source = "auth_outcome")
    @Mapping(target = "authMethod", source = "login_method")
    @Mapping(target = "failureReason", source = "failure_reason")
    LoginAttemptsDetails mapToLoginInAttemptsDetails(Login login);
}
