package com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.model;

public enum ResponseCodeV2 {
    BLOCKED("BLOCKED"),
    REJECTED("REJECTED"),
    PASSED("PASSED"),
    REFERRED("REFERRED"),
    CANCELLED("CANCELLED");

    private String respString;

    ResponseCodeV2(String respString) {
        this.respString = respString;
    }

    @Override
    public String toString() {
        return respString;
    }
}