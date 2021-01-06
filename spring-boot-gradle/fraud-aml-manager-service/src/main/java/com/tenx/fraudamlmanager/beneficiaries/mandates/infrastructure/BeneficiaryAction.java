package com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure;

public enum BeneficiaryAction {
    SETUP("SETUP"),
    CANCELLATION("CANCELLATION");

    private String enumString;

    BeneficiaryAction(String enumString) {
        this.enumString = enumString;
    }
}
