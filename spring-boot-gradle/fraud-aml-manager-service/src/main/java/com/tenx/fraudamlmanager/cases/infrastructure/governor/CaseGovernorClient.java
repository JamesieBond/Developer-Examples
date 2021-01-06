package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseCreationResult;
import com.tenx.fraudamlmanager.cases.domain.external.ExternalCaseUpdateResult;
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.external.ExternalCaseUpdateRequest;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.internal.InternalCasesRequest;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.CasesListV2Request;
import com.tenx.fraudamlmanager.payments.cases.infrastructure.CaseClientConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CaseGovernor", url = "${casegovernor.url}", configuration = CaseClientConfig.class)
public interface CaseGovernorClient {

  @PostMapping(value = "/case-governor/v1/cases", consumes = "application/json", produces = "application/json")
  List<CaseCreationResponse> createInternalCase(@RequestBody InternalCasesRequest internalCasesRequest)
      throws CaseGovernorException;

  @PostMapping(value = "/case-governor/v1/cases", consumes = "application/vnd.external+json", produces = "application/json")
  ExternalCaseCreationResult createExternalCase(@RequestBody ExternalCaseRequest externalCase)
      throws CaseGovernorException;

  @PostMapping(value = "/case-governor/v1/cases", consumes = "application/json", produces = "application/json")
  List<CaseCreationResponse> createCasesV2(@RequestBody CasesListV2Request casesListV2Request)
      throws CaseGovernorException;

  @PostMapping(value = "/case-governor/v1/cases/{bpmSystemCaseId}", consumes = "application/json", produces = "application/json")
  ExternalCaseUpdateResult updateExternalCase(
      @PathVariable(value = "bpmSystemCaseId") String bpmSystemCaseId,
      @RequestBody ExternalCaseUpdateRequest externalCase) throws CaseGovernorException;

}