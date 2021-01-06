package com.tenx.fraudamlmanager.cases.domain.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCaseCreationResult {

  private String bpmSystemCaseId;
  private String tenxCaseId;
  private String partyKey;
  private String bpmSystem;
}
