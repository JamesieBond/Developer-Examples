package com.tenx.fraudamlmanager.paymentsv3.api;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsRequestV3 {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String accountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String bankId;
}
