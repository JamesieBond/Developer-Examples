package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private Agent agent;
  private Account account;
}
