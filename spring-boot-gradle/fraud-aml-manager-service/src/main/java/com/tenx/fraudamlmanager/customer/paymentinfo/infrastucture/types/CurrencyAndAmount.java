package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.types;


import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyAndAmount {

  private BigDecimal value;
  private String currency;
}
