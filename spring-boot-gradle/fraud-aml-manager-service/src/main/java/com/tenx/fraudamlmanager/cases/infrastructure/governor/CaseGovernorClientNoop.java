package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseCreationResult;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseUpdateResult;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseUpdateResultDetails;
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseUpdateRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.internal.InternalCasesRequest;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.CasesListV2Request;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@ConditionalOnProperty(value = "casegovernor.enableMock", havingValue = "true")
public class CaseGovernorClientNoop implements CaseGovernorClient {

  @Override
  public List<CaseCreationResponse> createInternalCase(
      @RequestBody InternalCasesRequest newInternalCases) {
    CaseCreationResponse caseCreationResponse =
        new CaseCreationResponse(
            UUID.randomUUID().toString(),
            newInternalCases.getCases().stream().findFirst().get().getPrimaryPartyKey());
    List<CaseCreationResponse> caseCreationResponses = new ArrayList<>();
    caseCreationResponses.add(caseCreationResponse);
    return caseCreationResponses;
  }

  @Override
  public ExternalCaseCreationResult createExternalCase(ExternalCaseRequest externalCase) {
    return new ExternalCaseCreationResult(externalCase.getBpmSystemCaseId(),
        UUID.randomUUID().toString(), externalCase.getPrimaryPartyKey(),
        externalCase.getBpmSystem());
  }

  @Override
  public List<CaseCreationResponse> createCasesV2(@RequestBody CasesListV2Request newCase) {
    CaseCreationResponse caseCreationResponse =
        new CaseCreationResponse(
            UUID.randomUUID().toString(),
            newCase.getCases().stream().findFirst().get().getPrimaryPartyKey());
    List<CaseCreationResponse> caseCreationResponses = new ArrayList<>();
    caseCreationResponses.add(caseCreationResponse);
    return caseCreationResponses;
  }

  @Override
  public ExternalCaseUpdateResult updateExternalCase(
      @PathVariable(value = "bpmSystemCaseId") String bpmSystemCaseId,
      @RequestBody ExternalCaseUpdateRequest externalCase) {
    return new ExternalCaseUpdateResult(
        new ExternalCaseUpdateResultDetails(bpmSystemCaseId, UUID.randomUUID().toString()));
  }
}
