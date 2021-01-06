package com.tenx.fraudamlmanager.subscriptions.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class ProductDetails {

  private String productType;

  private String tenantKey;

  private Limits limits;

}
