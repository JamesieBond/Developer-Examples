package com.tenx.fraudamlmanager.authentication.stepup.infrastructure;


public enum StepUpAuthOutcome {

    STEPUP_FAILED("STEPUP_FAILED"),
    STEPUP_SUCCESS("STEPUP_SUCCESS");

    private String respString;

    StepUpAuthOutcome(String respString) {
        this.respString = respString;
    }

}
