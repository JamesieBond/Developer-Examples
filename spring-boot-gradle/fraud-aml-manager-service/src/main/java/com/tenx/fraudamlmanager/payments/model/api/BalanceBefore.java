package com.tenx.fraudamlmanager.payments.model.api;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceBefore {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String currency;

    @NotNull
    @ApiModelProperty(required = true)
    private double value;

    @NotNull
    @ApiModelProperty(required = true)
    private String baseCurrency;

    @NotNull
    @ApiModelProperty(required = true)
    private double baseValue;

}