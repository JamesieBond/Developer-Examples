package com.tenx.fraudamlmanager.paymentsv3.domestic.api;

import com.tenx.fraudamlmanager.paymentsv3.api.AccountDetailsRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.BalanceBeforeRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.PaymentAmountRequestV3;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO object for DomesticIn payments.
 *
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class DomesticInPaymentRequestV3 {

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

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private PaymentAmountRequestV3 amount;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private BalanceBeforeRequestV3 balanceBefore;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    @NotNull
    @ApiModelProperty(required = true, value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date transactionDate;

    @ApiModelProperty(value = "format: yyyy-MM-dd'T'hh:mm:ss.SSSZ", example = "2019-11-21T12:35:24Z")
    private Date messageDate;

    private String transactionStatus;

    private String transactionReference;

    private String creditorPartyKey;

    public String partyKeyFromDebtorAccount() {
        AccountDetailsRequestV3 accountDetails = this.getDebtorAccountDetails();

        if (accountDetails == null) {
            return "";
        }

        String accountNumber = accountDetails.getAccountNumber();
        String bankId = accountDetails.getBankId();
        String partyKey = "";

        if (accountNumber != null && !accountNumber.isEmpty() && bankId != null && !bankId.isEmpty()) {
            partyKey = accountNumber + " " + bankId;
        } else if (accountNumber != null) {
            partyKey = accountNumber;
        } else if (bankId != null) {
            partyKey = bankId;
        }
        return partyKey;
    }

}
