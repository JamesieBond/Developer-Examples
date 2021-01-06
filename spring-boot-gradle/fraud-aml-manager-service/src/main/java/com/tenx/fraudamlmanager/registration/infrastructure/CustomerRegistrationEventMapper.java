package com.tenx.fraudamlmanager.registration.infrastructure;

import com.tenx.security.forgerockfacade.resource.CustomerRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerRegistrationEventMapper {

    CustomerRegistrationEventMapper MAPPER = Mappers.getMapper(CustomerRegistrationEventMapper.class);
    RegistrationDetails toRegistrationDetails(CustomerRegistration customerRegistration);
}
