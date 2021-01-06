package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseCreationResult;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CaseGovernorResponseMapper {

  CaseGovernorResponseMapper MAPPER = Mappers.getMapper(CaseGovernorResponseMapper.class);

  List<CaseCreationResult> toCaseCreationResult(List<CaseCreationResponse> caseCreationResponses);

  CaseCreationResult toCaseCreationResult(CaseCreationResponse caseCreationResponses);

}
