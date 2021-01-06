package com.tenx.fraudamlmanager.cases.v2.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CasesListV2 {
    private List<CaseV2> cases = new ArrayList<>();

    public void add(CaseV2 caseObj) {
        cases.add(caseObj);
    }

}
