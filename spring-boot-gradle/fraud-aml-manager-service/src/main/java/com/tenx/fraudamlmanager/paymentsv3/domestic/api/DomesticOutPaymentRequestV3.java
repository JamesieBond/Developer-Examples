package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import com.tenx.fraudamlmanager.paymentsv3.api.AccountDetailsRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.BalanceBeforeRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.PaymentAmountRequestV3;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
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
class DomesticOutPaymentRequestV3 {

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private AccountDetailsRequestV3 creditorAccountDetails;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorName;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private AccountDetailsRequestV3 debtorAccountDetails;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorName;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorPartyKey;

    @Valid
    @ApiModelProperty(required = true)
    private PaymentAmountRequestV3 amount;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    @NotNull
    @ApiModelProperty(required = true, value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date transactionDate;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private BalanceBeforeRequestV3 balanceBefore;

    @ApiModelProperty(value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date messageDate;

    private String transactionStatus;

    private String transactionReference;

    private String transactionNotes;

    private List<String> transactionTags;

    private Boolean existingPayee;

}
