package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseCreationResult;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CaseGovernorResponseMapperV2 {

    CaseGovernorResponseMapperV2 MAPPER = Mappers.getMapper(CaseGovernorResponseMapperV2.class);

    List<CaseCreationResult> toCaseCreationResult(List<CaseCreationResponse> caseV2);

}
