package com.tenx.fraudamlmanager.payments.model.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO object for on-Us payments.
 *
 * @author Massimo Della Rovere
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "OnUsPayment")
public class OnUsPayment {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorAccountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorName;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorSortCode;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorAccountNumber;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorName;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorSortCode;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private BalanceBefore balanceBefore;

    @NotNull
    @ApiModelProperty(required = true)
    private Double instructedAmount;

    private String transactionCurrencyCode;

    private Double interbankSettlementAmount;

    private String interbankSettlmentCurrency;

    private Double maximumPaymentLimit;

    @ApiModelProperty(value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date messageCreationDateTime;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String baseCurrencyCode;

    private String creditorPartyKey;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorPartyKey;

    @NotNull
    @ApiModelProperty(required = true, value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date transactionDate;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    private String creditorSubscriptionKey;

    private String debtorSubscriptionKey;

    private String creditorSubscriptionStatus;

    private String debtorSubscriptionStatus;

    private String transactionReference;

    @ApiModelProperty(value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date valueDateTime;

    @ApiModelProperty(value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date requestedExecutionDate;

    private String message;

    private String originalCurrency;

    private String convertedCurrency;

    @ApiModelProperty(value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date initiationDateTime;

}
