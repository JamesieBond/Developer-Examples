package com.tenx.fraudamlmanager.cases.infrastructure.governor.external;

import com.tenx.fraudamlmanager.cases.domain.external.ExternalCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExternalCasesMapper {

  ExternalCasesMapper MAPPER = Mappers.getMapper(ExternalCasesMapper.class);

  @Mapping(target = "bpmSystemCaseId", ignore = true)
  ExternalCaseRequest toExternalCaseRequest(ExternalCase externalCase);

}
