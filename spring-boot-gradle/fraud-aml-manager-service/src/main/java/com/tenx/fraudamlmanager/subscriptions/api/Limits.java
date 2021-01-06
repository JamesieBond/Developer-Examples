package com.tenx.fraudamlmanager.subscriptions.api;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class Limits {

  private List<TransactionLimits> transactionLimits;

  private List<SchemeLimits> schemeLimits;

  private List<AccountBalanceLimits> accountBalanceLimits;

  private List<ProductLimits> productLimits;

  private List<FundingLimits> fundingLimits;

}
