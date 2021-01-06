package com.tenx.fraudamlmanager.payments.cases.infrastructure;

import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.v1.domain.CasesList;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.CasesListV2Request;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CaseGovernorOld", url = "${casegovernor.url}", configuration = CaseClientConfig.class)
public interface CaseGovernorClient {
    //todo: create separate CaseGovernorClientV2

  @PostMapping(value = "/case-governor/v1/cases", consumes = "application/json", produces = "application/json")
  List<CaseCreationResponse> createCases(@RequestBody CasesList newCase);

  @PostMapping(value = "/case-governor/v1/cases", consumes = "application/json", produces = "application/json")
  List<CaseCreationResponse> createCasesV2(@RequestBody CasesListV2Request newCase);

}