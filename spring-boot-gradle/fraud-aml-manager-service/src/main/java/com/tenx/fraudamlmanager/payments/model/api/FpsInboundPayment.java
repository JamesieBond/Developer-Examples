package com.tenx.fraudamlmanager.payments.model.api;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FpsInboundPayment {

    private String transactionStatus;

    private String partyKey;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private BalanceBefore balanceBefore;

    @NotNull
    @ApiModelProperty(required = true, value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date transactionDate;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorAccountName;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorSortCode;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorAccountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorAccountNumber;

    @NotNull
    @ApiModelProperty(required = true)
    private Double instructedAmount;

    private Double interbankSettlementAmount;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String instructedAmountCurrency;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorSortCode;

    private String transactionReference;

    private String interbankSettlementCurrency;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorName;

}
