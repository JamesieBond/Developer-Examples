package com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure;

public enum AuthMethod {

    PASSCODE ("PASSCODE"),
    BIOMETRIC ("BIOMETRIC");

    private String respString;

    AuthMethod(String respString) {
        this.respString = respString;
    }

}
