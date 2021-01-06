package com.tenx.fraudamlmanager.paymentsv3.domain;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudCheckV3 {

    @NotEmpty
    private FraudAMLSanctionsCheckResponseCodeV3 status;

    private final List<ExternalCaseDetailsV3> externalCases = new ArrayList<>();
}
