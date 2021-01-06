package com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.model;

public enum ResponseCodeV3 {
    BLOCKED("BLOCKED"),
    REJECTED("REJECTED"),
    PASSED("PASSED"),
    REFERRED("REFERRED"),
    CANCELLED("CANCELLED");

    private String respString;

    ResponseCodeV3(String respString) {
        this.respString = respString;
    }

    @Override
    public String toString() {
        return respString;
    }
}
