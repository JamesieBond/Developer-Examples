package com.tenx.fraudamlmanager.authentication.loginattempts.infrastructure;

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
public class LoginAttempts {

    @NotEmpty
    @NotNull
    private String partyKey;

    @NotNull
    private AuthOutcome authOutcome;

    @NotNull
    private AuthMethod authMethod;

    private String failureReason;
}
