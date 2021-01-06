package com.tenx.fraudamlmanager.paymentsv3.domain;


/**
 * @author Niall O'Connell
 */
public enum FraudAMLSanctionsCheckResponseCodeV3 {
    blocked,
    passed,
    rejected,
    referred,
    pending,
    cancelled;
}
