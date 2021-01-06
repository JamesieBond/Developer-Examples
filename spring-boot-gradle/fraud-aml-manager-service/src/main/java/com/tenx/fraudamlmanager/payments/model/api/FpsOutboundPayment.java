package com.tenx.fraudamlmanager.payments.model.api;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FpsOutboundPayment {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorName;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorAccountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorSortCode;

    private String debtorAccountNumberCode;

    private String creditorAccountNumberCode;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorName;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorAccountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorSortCode;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private BalanceBefore balanceBefore;

    @NotNull
    @ApiModelProperty(required = true)
    private Double instructedAmount;

    private String transactionCurrencyCode;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String baseCurrencyCode;

    private String partyKey;

    private String transactionStatus;

    @NotNull
    @ApiModelProperty(required = true, value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date transactionDate;

    private String transactionReference;

    @ApiModelProperty(value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date messageCreationDateTime;

}
