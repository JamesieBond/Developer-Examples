package com.tenx.fraudamlmanager.paymentsv2.onus.api;

import io.swagger.annotations.ApiModelProperty;
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
public class PaymentAmountRequestOnUsV2 {

  @NotEmpty
  @ApiModelProperty(required = true)
  private String currency;

  @NotNull
  @ApiModelProperty(required = true)
  private double value;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String baseCurrency;

  @NotNull
  @ApiModelProperty(required = true)
  private double baseValue;

}
