package com.tenx.fraudamlmanager.subscriptions.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class SchemeLimits {

  private String schemeName;

  private String description;

  private String minimumAmount;

  private String maximumAmount;

  /**
   * DAY,MONTH
   */
  private String resetPeriod;

}
