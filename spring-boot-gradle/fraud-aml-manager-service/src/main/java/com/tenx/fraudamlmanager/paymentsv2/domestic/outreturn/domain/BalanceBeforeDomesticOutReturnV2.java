package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceBeforeDomesticOutReturnV2 {

  private String currency;

  private double value;

  private String baseCurrency;

  private double baseValue;
}
