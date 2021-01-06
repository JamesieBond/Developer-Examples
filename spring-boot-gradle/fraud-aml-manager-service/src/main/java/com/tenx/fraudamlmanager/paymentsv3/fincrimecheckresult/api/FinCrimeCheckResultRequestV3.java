package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.api;

import com.tenx.fraudamlmanager.paymentsv3.api.ExternalCaseDetailsRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV3;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Shruti Gupta
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinCrimeCheckResultRequestV3 {
    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    @NotNull
    @ApiModelProperty(required = true)
    private FinCrimeCheckResultResponseCodeV3 status;

    private List<ExternalCaseDetailsRequestV3> externalCases;
}
