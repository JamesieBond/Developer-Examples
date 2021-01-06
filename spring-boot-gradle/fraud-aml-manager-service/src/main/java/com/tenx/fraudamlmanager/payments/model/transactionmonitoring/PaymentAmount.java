package com.tenx.fraudamlmanager.payments.model.transactionmonitoring;

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
public class PaymentAmount {

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
    private double baseValue;

}
