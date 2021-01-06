package com.tenx.fraudamlmanager.paymentsv2.domestic.api;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsRequestV2 {

  @NotEmpty
  @ApiModelProperty(required = true)
  private String accountNumber;

  @NotEmpty
  @ApiModelProperty(required = true)
  private String bankId;

}
