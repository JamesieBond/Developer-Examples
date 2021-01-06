package com.tenx.fraudamlmanager.subscriptions.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class AccountBalanceLimits {

  private String balanceType;

  private String description;

  private String minimumAmount;

  private String maximumAmount;

}
