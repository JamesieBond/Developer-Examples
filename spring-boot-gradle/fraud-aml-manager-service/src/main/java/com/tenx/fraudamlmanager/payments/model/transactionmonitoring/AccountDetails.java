package com.tenx.fraudamlmanager.payments.model.transactionmonitoring;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String accountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String bankId;

}
