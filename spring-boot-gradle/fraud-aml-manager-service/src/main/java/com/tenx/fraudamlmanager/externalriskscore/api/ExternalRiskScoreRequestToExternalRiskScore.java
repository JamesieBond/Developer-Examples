package com.tenx.fraudamlmanager.externalriskscore.api;

import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExternalRiskScoreRequestToExternalRiskScore {

    ExternalRiskScoreRequestToExternalRiskScore MAPPER = Mappers
        .getMapper(ExternalRiskScoreRequestToExternalRiskScore.class);


    ExternalRiskScore toRiskScoreEvent(ExternalRiskScoreRequest externalRiskScoreRequest);
}
