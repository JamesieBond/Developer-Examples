package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CasesListV2Request {
    private List<CaseV2Request> cases = new ArrayList<>();
}
