package com.tenx.fraudamlmanager.cases.domain.internal;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalCases {

  private List<InternalCase> cases = new ArrayList<>();

  public void add(InternalCase caseObj) {
    cases.add(caseObj);
  }

}
