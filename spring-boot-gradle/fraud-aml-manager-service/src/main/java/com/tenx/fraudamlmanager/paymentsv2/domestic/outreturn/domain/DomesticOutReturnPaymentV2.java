package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomesticOutReturnPaymentV2 {

    private AccountDetailsDomesticOutReturnV2 creditorAccountDetails;

    private String creditorName;

    private AccountDetailsDomesticOutReturnV2 debtorAccountDetails;

    private String debtorName;

    private PaymentAmountDomesticOutReturnV2 amount;

    private String transactionId;

    private Date transactionDate;

    private Date messageDate;

    private String transactionStatus;

    private String transactionReference;

    private String transactionNotes;

    private List<String> transactionTags;

    private Boolean existingPayee;

    private BalanceBeforeDomesticOutReturnV2 balanceBefore;

    private String partyKey;

}
