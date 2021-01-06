package com.tenx.fraudamlmanager.authentication.stepup.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class StepUpDetails {

    @NotEmpty
    @NotNull
    private String partyKey;

    @NotEmpty
    @NotNull
    private String authOutcome;

    @NotEmpty
    @NotNull
    private String authMethod;

    private String failureReason;

    public boolean isAuthOutcomeApplicable() {
        if (authOutcome != null) {
            return (authOutcome.equals("STEPUP_FAILED") || authOutcome.equals("STEPUP_SUCCESS"));
        } else {
            return false;
        }
    }

    public boolean isAuthMethodApplicable() {
        if (authMethod != null) {
            return (authMethod.equals("PASSCODE") || authMethod.equals("BIOMETRIC"));
        } else {
            return false;
        }
    }


    public boolean isValidTmaPayment() {
        return (isAuthOutcomeApplicable() && isAuthMethodApplicable());
    }
}
