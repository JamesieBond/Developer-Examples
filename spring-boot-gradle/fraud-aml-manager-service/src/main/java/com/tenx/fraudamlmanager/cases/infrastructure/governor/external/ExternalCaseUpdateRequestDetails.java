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
public class ExternalCaseUpdateRequestDetails {

  private final String bpmSystem = "SALESFORCE";
  private List<CaseAttributeRequest> attributes = new ArrayList<>();
  private String colleagueId;
  private String outcome;
  private String status;
  private String subscriptionKey;
}