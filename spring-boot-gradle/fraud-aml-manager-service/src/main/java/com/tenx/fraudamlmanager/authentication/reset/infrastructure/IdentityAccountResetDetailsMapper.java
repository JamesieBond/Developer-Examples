package com.tenx.fraudamlmanager.authentication.reset.infrastructure;


import com.tenx.fraudamlmanager.authentication.reset.domain.AuthResetDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {IdentityAccountReset.class})
public interface IdentityAccountResetDetailsMapper {

    IdentityAccountResetDetailsMapper MAPPER = Mappers.getMapper(IdentityAccountResetDetailsMapper.class);

    AuthReset toAuthReset(AuthResetDetails authResetDetails);
}
