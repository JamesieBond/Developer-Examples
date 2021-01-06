package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import com.tenx.fraudamlmanager.cases.domain.external.ExternalCase;
import com.tenx.fraudamlmanager.cases.domain.internal.InternalCases;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseUpdateRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.internal.InternalCasesRequest;
import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomainInfrastructureCaseGovernorMapper {

  DomainInfrastructureCaseGovernorMapper MAPPER = Mappers
      .getMapper(DomainInfrastructureCaseGovernorMapper.class);

  InternalCasesRequest toInternalCases(CasesListV2 casesListV2);

  InternalCasesRequest toCaseGovernorRequest(InternalCases internalCases);

  ExternalCaseRequest toCaseGovernorExternal(ExternalCase externalCase);

  @Mapping(target = "externalCaseUpdateRequestDetails.attributes", source = "externalCase.attributes")
  @Mapping(target = "externalCaseUpdateRequestDetails.outcome", source = "externalCase.outcome")
  @Mapping(target = "externalCaseUpdateRequestDetails.status", source = "externalCase.status")
  @Mapping(target = "externalCaseUpdateRequestDetails.colleagueId", source = "externalCase.colleagueId")
  @Mapping(target = "externalCaseUpdateRequestDetails.subscriptionKey", source = "externalCase.subscriptionKey")
  ExternalCaseUpdateRequest toCaseGovernorExternalUpdate(ExternalCase externalCase);

}
