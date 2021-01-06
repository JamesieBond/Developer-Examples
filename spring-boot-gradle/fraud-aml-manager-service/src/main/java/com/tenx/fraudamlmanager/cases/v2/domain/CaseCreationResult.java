package com.tenx.fraudamlmanager.cases.v2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCreationResult {

    String caseId;

    String partyKey;

}