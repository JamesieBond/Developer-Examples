package com.tenx.fraudamlmanager.payments.model.transactionmonitoringV2;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsV2 {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String accountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String bankId;

}
