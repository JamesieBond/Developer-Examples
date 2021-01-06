package com.tenx.fraudamlmanager.cases.domain;

import com.tenx.fraudamlmanager.cases.domain.external.ExternalCase;
import com.tenx.fraudamlmanager.cases.domain.internal.InternalCase;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CaseMapper {

  CaseMapper MAPPER = Mappers.getMapper(CaseMapper.class);

  InternalCase toInternalCase(CaseV2 caseV2);

  @Mapping(target = "bpmSystem", ignore = true)
  @Mapping(target = "bpmSystemCaseId", ignore = true)
  @Mapping(target = "colleagueId", ignore = true)
  @Mapping(target = "displayToCustomer", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "outcome", ignore = true)
  ExternalCase toExternalCase(CaseV2 caseV2);


  @Named("mapExternalCaseUpdateOutcome")
  static String mapExternalCaseUpdateOutcome(FinCrimeCheckCaseStatus finCrimeCheckCaseStatus) {
    if (FinCrimeCheckCaseStatus.PASSED.equals(finCrimeCheckCaseStatus)) {
      return "PAYMENT SENT TO BENEFICIARY";
    } else if (FinCrimeCheckCaseStatus.REJECTED.equals(finCrimeCheckCaseStatus)
        || FinCrimeCheckCaseStatus.CANCELLED.equals(finCrimeCheckCaseStatus)) {
      return "PAYMENT SENT TO ORIGINATOR";
    } else if (FinCrimeCheckCaseStatus.BLOCKED.equals(finCrimeCheckCaseStatus)) {
      return "PAYMENT HELD INDEFINITELY";
    } else {
      throw new IllegalArgumentException("Unknown FinCrimeCheckCaseStatus.");
    }
  }

  @Mapping(target = "outcome", source = "finCrimeCheckCase.status", qualifiedByName = "mapExternalCaseUpdateOutcome")
  @Mapping(target = "bpmSystemCaseId", source = "caseId")
  @Mapping(target = "bpmSystem", ignore = true)
  @Mapping(target = "colleagueId", ignore = true)
  @Mapping(target = "displayToCustomer", ignore = true)
  ExternalCase toExternalCaseForUpdate(CaseV2 caseV2, FinCrimeCheckCase finCrimeCheckCase,
      String caseId);

}
