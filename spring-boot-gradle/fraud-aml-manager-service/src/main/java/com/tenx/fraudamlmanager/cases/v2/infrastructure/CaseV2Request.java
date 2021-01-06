package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseV2Request {

    public String caseType;
    public List<CaseAttributeV2Request> attributes = new ArrayList<>();
    private String primaryPartyKey;
    private String secondaryPartyKey;
    private String subscriptionKey;
}
