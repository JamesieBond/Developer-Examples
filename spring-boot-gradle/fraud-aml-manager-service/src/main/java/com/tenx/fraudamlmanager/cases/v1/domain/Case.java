package com.tenx.fraudamlmanager.cases.v1.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tenx.fraudamlmanager.cases.domain.CaseAttribute;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Case {

    public String caseType;
    private List<CaseAttribute> attributes = new ArrayList<CaseAttribute>();
    private String primaryPartyKey;
    private String secondaryPartyKey;
    private String subscriptionKey;

    public void add(String name, String val) {

        if (val != null && !val.isEmpty()) {
            attributes.add(new CaseAttribute(name, val));
        }
    }

    public void add(String name, Boolean val) {

        if (val != null) {
            attributes.add(new CaseAttribute(name, Boolean.toString(val)));
        }
    }

    public void add(String name, Double val) {

        if (val != null) {
            attributes.add(new CaseAttribute(name, Double.toString(val)));
        }
    }

    public enum CaseType {
        FRAUD_EXCEPTION
    }
}
