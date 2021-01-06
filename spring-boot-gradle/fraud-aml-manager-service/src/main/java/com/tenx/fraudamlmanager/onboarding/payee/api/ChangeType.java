package com.tenx.fraudamlmanager.onboarding.payee.api;

public enum ChangeType {
    UPDATE("update"),
    DELETE("delete"),
    CREATE("create");

    private String respString;

    ChangeType(String respString) {
        this.respString = respString;
    }
}
