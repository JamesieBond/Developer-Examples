package com.tenx.fraudamlmanager.externalriskscore.api;

import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScore;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreException;
import com.tenx.fraudamlmanager.externalriskscore.domain.ExternalRiskScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author James Spencer
 */
@RestController
@RequestMapping("/v1")
@Slf4j
@Api(tags = "External Risk Score API")
public class ExternalRiskScoreController {

    @Autowired
    private ExternalRiskScoreService externalRiskScoreService;

    /**
     * This end-point External Risk Score
     */
    @ApiOperation(value = "Update the Risk Score for a particular Party")
    @PostMapping("/externalRiskScore")
    public void updateExternalRiskScore(@RequestBody @Valid ExternalRiskScoreRequest externalRiskScoreRequest)
        throws ExternalRiskScoreAPIException {
        try {
            log.info("External Risk Score request Received for ID: {}", externalRiskScoreRequest.getPartyKey());
            ExternalRiskScore externalRiskScore = ExternalRiskScoreRequestToExternalRiskScore.MAPPER
                .toRiskScoreEvent(externalRiskScoreRequest);
            externalRiskScoreService.generateAndStoreRiskScoreEvent(externalRiskScore);
        } catch (ExternalRiskScoreException ex) {
            throw new ExternalRiskScoreAPIException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ex);
        }
    }

}
