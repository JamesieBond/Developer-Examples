package com.tenx.fraudamlmanager.cases.v2.domain;

import java.util.List;

public interface CaseGovernorConnectorV2 {
    List<CaseCreationResult> createInternalCases(CasesListV2 casesListV2);
}


