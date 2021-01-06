package com.tenx.fraudamlmanager.authentication.reset.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class AuthResetDetails {

    @NotEmpty
    @NotNull
    private String partyKey;

    @NotEmpty
    @NotNull
    private String result;

    public boolean isIdentityAccountResetApplicable() {
        if (result != null) {
            return (result.equals("FAILED") || result.equals("PASSED"));
        } else {
            return false;
        }
    }
}
