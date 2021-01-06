package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CaseListMapperV2 {

    CaseListMapperV2 MAPPER = Mappers.getMapper(CaseListMapperV2.class);

    CasesListV2Request toCaseGovernorRequest(CasesListV2 caseV2);

}
