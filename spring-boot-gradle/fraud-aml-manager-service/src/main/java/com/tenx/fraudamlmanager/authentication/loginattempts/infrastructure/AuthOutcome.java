package com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure;


public enum AuthOutcome {

    FAILED ("FAILED"),
    SUCCESS ("SUCCESS");

    private String respString;

    AuthOutcome(String respString) {
        this.respString = respString;
    }

}
