package com.tenx.fraudamlmanager.authentication.reset.infrastructure;

public enum IdentityAccountReset {

    PASSED("PASSED"),
    FAILED("FAILED");

    private String enumString;

    IdentityAccountReset(String enumString) {
        this.enumString = enumString;
    }
}
