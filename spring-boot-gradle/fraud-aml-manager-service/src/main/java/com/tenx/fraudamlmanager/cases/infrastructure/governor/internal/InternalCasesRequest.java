package com.tenx.fraudamlmanager.cases.infrastructure.governor.internal;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalCasesRequest {

  private List<InternalCaseRequest> cases = new ArrayList<>();

  public void add(InternalCaseRequest caseObj) {
    cases.add(caseObj);
  }

}
