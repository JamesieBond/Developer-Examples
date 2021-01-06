package com.tenx.fraudamlmanager.cases.domain.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalCaseUpdateResultDetails {

  private String bpmSystemCaseId;
  private String tenxCaseId;
}
