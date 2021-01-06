package com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma;

import com.tenx.fraudamlmanager.paymentsv3.domain.ExternalCaseDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Value;

/**
 * @author Niall O'Connell
 */
@Value
public class FraudAMLSanctionsCheckResponseV3 {

    @NotEmpty
    @ApiModelProperty(required = true)
    private FraudAMLSanctionsCheckResponseCodeV3 status;

    private final List<ExternalCaseDetailsV3> externalCases = new ArrayList<>();
}
