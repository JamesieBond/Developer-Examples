package com.tenx.fraudamlmanager.cases.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaseCreationResponse {

    @JsonProperty("10x_case_id")
    String caseId;

    String partyKey;

}