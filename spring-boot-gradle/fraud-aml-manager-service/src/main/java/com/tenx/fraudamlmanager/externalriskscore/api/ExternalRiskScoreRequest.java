package com.tenx.fraudamlmanager.externalriskscore.api;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class ExternalRiskScoreRequest {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String partyKey;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String riskScore;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String provider;

}
