package com.tenx.fraudamlmanager.cases.v1.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CasesList {
    private List<Case> cases = new ArrayList<Case>();

    public void add(Case caseObj) {
        cases.add(caseObj);
    }

}
