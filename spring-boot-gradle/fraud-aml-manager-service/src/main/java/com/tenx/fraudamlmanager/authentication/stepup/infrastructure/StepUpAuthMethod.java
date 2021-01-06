package com.tenx.fraudamlmanager.authentication.stepup.infrastructure;

public enum StepUpAuthMethod {

    PASSCODE ("PASSCODE"),
    BIOMETRIC ("BIOMETRIC");

    private String respString;

    StepUpAuthMethod(String respString) {
        this.respString = respString;
    }

}
