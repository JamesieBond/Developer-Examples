package com.tenx.fraudamlmanager.payments.model.transactionmonitoringV2;

/**
 * @author Niall O'Connell
 */

public enum FraudAMLSanctionsCheckResponseCodeV2 {
    BLOCKED("BLOCKED"),
    REJECTED("REJECTED"),
    PASSED("PASSED"),
    REFERRED("REFERRED"),
    CANCELLED("CANCELLED");

    private String respString;

    FraudAMLSanctionsCheckResponseCodeV2(String respString) {
        this.respString = respString;
    }
}
