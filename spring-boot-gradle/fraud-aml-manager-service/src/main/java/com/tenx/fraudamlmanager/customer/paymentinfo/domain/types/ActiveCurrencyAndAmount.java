package com.tenx.fraudamlmanager.customer.paymentinfo.domain.types;


import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveCurrencyAndAmount {


  private BigDecimal value;
  private String currency;
}
