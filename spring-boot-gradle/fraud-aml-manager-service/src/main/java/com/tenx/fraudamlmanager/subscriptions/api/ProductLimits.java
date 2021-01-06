package com.tenx.fraudamlmanager.subscriptions.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class ProductLimits {

    private String productKey;

    private String productName;

    private String productType;

    private String description;

    private Integer maximumNumber;
}