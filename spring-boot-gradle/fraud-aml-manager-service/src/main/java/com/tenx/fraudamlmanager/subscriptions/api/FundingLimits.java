package com.tenx.fraudamlmanager.subscriptions.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class FundingLimits {

  private String fundingMechanism;

  private String description;

  private String minimumAmount;

  private String maximumAmount;

  private String defaultAmount;

}
