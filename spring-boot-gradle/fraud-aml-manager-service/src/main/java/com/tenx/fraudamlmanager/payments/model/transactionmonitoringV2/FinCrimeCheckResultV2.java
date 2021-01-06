package com.tenx.fraudamlmanager.payments.model.transactionmonitoringV2;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.model.ResponseCodeV2;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Value;

/**
 * @author Niall O'Connell
 */
@Value
public class FinCrimeCheckResultV2 {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private ResponseCodeV2 status;

    private List<ExternalCaseDetailsV2> externalCases;

}
