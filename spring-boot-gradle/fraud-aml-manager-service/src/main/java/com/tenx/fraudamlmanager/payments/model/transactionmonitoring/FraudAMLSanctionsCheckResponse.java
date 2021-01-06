package com.tenx.fraudamlmanager.payments.model.transactionmonitoring;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Value;

/**
 * @author Niall O'Connell
 */
@Value
public class FraudAMLSanctionsCheckResponse {

    public enum ResponseCode {
        BLOCKED("Blocked"),
        REJECTED("Rejected"),
        PASSED("Passed"),
        REFERRED("Referred"),
        CANCELLED("Cancelled");

        private String respString;

        ResponseCode(String respString) {
            this.respString = respString;
        }

        @Override
        public String toString() {
            return respString;
        }
    }

    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String status;

    private List<ExternalCaseDetails> externalCases;

}
