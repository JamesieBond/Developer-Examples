package com.tenx.fraudamlmanager.paymentsv3.domain;

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
public class BalanceBeforeV3 {

    @NotEmpty
    private String currency;

    @NotNull
    private double value;

    @NotEmpty
    private String baseCurrency;

    @NotNull
    private double baseValue;
}
