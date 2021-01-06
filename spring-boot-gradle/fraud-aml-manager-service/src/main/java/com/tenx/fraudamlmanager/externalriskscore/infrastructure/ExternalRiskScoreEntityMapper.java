package com.tenx.fraudamlmanager.externalriskscore.infrastructure;

import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExternalRiskScoreEntityMapper {

    ExternalRiskScoreEntityMapper MAPPER = Mappers.getMapper(ExternalRiskScoreEntityMapper.class);

    @Mapping(target = "externalRiskScoreJson", source = "externalRiskScore")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    ExternalRiskScoreEntity toRiskScoreEntity(ExternalRiskScore externalRiskScore);

}
