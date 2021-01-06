package com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain;

import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.AccountDetailsV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.BalanceBeforeV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.domain.PaymentAmountV2;
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
public class DomesticInPaymentV2 {

    @Valid
    @NotNull
    private AccountDetailsV2 creditorAccountDetails;

    @NotEmpty
    private String creditorName;

    private String creditorPostalAddress;

    @Valid
    @NotNull
    private AccountDetailsV2 debtorAccountDetails;

    @NotEmpty
    private String debtorName;

    private String debtorPostalAddress;

    @Valid
    @NotNull
    private PaymentAmountV2 amount;

    @NotEmpty
    private String transactionId;

    @NotNull
    private Date transactionDate;

    @Valid
    @NotNull
    private BalanceBeforeV2 balanceBefore;

    private Date messageDate;

    private String transactionStatus;

    private String transactionReference;

    private String paymentType;

    private String originatingCreditInstitution;

    private String partyKey;

    public String partyKeyFromDebtorAccount() {
        AccountDetailsV2 accountDetails = this.getDebtorAccountDetails();

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
