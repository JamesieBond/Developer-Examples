package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.api;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinCrimeCheckResultRequestV2 {

  @NotEmpty
  @ApiModelProperty(required = true)
  private String transactionId;

  @NotNull
  @ApiModelProperty(required = true)
  private FraudAMLSanctionsCheckResponseCodeV2 status;

  private List<ExternalCaseDetailsRequestV2> externalCases;

}
