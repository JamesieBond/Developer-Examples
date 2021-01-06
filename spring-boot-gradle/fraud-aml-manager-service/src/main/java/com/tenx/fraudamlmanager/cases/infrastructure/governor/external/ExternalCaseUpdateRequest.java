package com.tenx.fraudamlmanager.cases.infrastructure.governor.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalCaseUpdateRequest {

  @JsonProperty("case")
  private ExternalCaseUpdateRequestDetails externalCaseUpdateRequestDetails;
}