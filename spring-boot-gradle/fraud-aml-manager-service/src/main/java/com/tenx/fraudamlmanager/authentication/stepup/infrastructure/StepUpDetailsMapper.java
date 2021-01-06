package com.tenx.fraudamlmanager.authentication.stepup.infrastructure;

import com.tenx.fraudamlmanager.authentication.stepup.domain.StepUpDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {StepUpAuthOutcome.class, StepUpAuthMethod.class})
public interface StepUpDetailsMapper {

    StepUpDetailsMapper MAPPER = Mappers.getMapper(
        StepUpDetailsMapper.class);

    StepUpPayload mapToStepUp(StepUpDetails stepUpDetails);
}

