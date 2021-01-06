package com.tenx.fraudamlmanager.externalriskscore.domain;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalRiskScore {

    @NotEmpty
    private String partyKey;

    @NotEmpty
    private String riskScore;

    @NotEmpty
    private String provider;

}
