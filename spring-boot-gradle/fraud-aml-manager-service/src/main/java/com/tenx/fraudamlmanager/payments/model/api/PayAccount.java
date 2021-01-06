package com.tenx.fraudamlmanager.payments.model.api;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zaid Anwer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayAccount {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String accountName;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String accountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String bankId;

}
