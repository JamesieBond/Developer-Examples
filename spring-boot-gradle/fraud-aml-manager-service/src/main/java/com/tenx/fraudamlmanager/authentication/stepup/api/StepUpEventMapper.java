package com.tenx.fraudamlmanager.authentication.stepup.api;

import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpDetails;
import com.tenx.security.forgerockfacade.resource.StepUp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StepUpEventMapper {
    StepUpEventMapper MAPPER = Mappers.getMapper(
        StepUpEventMapper.class);

    @Mapping(target = "partyKey", source = "party_key")
    @Mapping(target = "authOutcome", source = "auth_outcome", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "authMethod", source = "auth_method", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "failureReason", source = "transaction_failure_reason")
    StepUpDetails mapToStepUpDetails(StepUp stepUp);
}

