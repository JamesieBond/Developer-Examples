package com.tenx.fraudamlmanager.authentication.stepup.infrastructure;

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
public class StepUpPayload {

    @NotEmpty
    @NotNull
    private String partyKey;

    @NotNull
    private StepUpAuthOutcome authOutcome;

    @NotNull
    private StepUpAuthMethod authMethod;

    private String failureReason;
}
