package com.tenx.fraudamlmanager.cases.infrastructure.governor.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseAttributeRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalCaseRequest {

  public String caseType;
  public List<CaseAttributeRequest> attributes = new ArrayList<>();
  private String primaryPartyKey;
  private String secondaryPartyKey;
  private String subscriptionKey;
  private String bpmSystem;
  private String bpmSystemCaseId;
  private String colleagueId;
  private String displayToCustomer;
  private String status;
}
