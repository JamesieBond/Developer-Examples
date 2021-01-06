package com.tenx.fraudamlmanager.payments.model.transactionmonitoring;

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
 * POJO object for DomesticOut payments.
 *
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomesticOutPayment {

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private AccountDetails creditorAccountDetails;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String creditorName;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private AccountDetails debtorAccountDetails;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String debtorName;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private PaymentAmount amount;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private BalanceBefore balanceBefore;

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

    private String transactionNotes;

    private List<String> transactionTags;

    private Boolean existingPayee;

    private String partyKey;

    public String partyKeyFromDebtorAccount() {
        AccountDetails accountDetails = this.getDebtorAccountDetails();

        if (accountDetails == null) {
            return "";
        }

        String accountNumber = accountDetails.getAccountNumber();
        String bankId = accountDetails.getBankId();
        String extractedPartyKey = "";

        if (accountNumber != null && bankId != null) {
            extractedPartyKey = accountNumber + " " + bankId;
        } else if (accountNumber != null) {
            extractedPartyKey = accountNumber;
        } else if (bankId != null) {
            extractedPartyKey = bankId;
        }
        return extractedPartyKey;
    }

}
