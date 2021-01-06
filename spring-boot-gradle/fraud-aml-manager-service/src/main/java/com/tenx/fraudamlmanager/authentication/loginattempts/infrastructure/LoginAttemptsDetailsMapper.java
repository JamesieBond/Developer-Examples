package com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure;

import com.tenx.fraudamlmanager.authentication.loginattempts.domain.LoginAttemptsDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(imports = {AuthOutcome.class, AuthMethod.class})
public interface LoginAttemptsDetailsMapper {

    LoginAttemptsDetailsMapper MAPPER = Mappers.getMapper(LoginAttemptsDetailsMapper.class);

    LoginAttempts mapToLoginInAttempts(LoginAttemptsDetails loginAttemptsDetails);
}
