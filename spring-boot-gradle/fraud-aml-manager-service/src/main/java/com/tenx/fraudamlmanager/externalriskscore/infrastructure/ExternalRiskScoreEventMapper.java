package com.tenx.fraudamlmanager.externalriskscore.infrastructure;

import com.tenx.fraud.ExternalRiskScoreEvent;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExternalRiskScoreEventMapper {

    ExternalRiskScoreEventMapper MAPPER = Mappers.getMapper(ExternalRiskScoreEventMapper.class);

    @Mapping(target = "eventTime", expression = "java( java.time.Instant.now().toString())")
    ExternalRiskScoreEvent toRiskScoreEvent(ExternalRiskScore externalRiskScore);

}
