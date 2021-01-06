package com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectDebitBacsPaymentV2 {

    private AccountDetailsDirectDebitV2 creditorAccountDetails;

    private String creditorName;

    private AccountDetailsDirectDebitV2 debtorAccountDetails;

    private String debtorName;

    private PaymentAmountDirectDebitV2 amount;

    private String partyKey;

    private String transactionId;

    private Date transactionDate;

    private Date messageDate;

    private String transactionStatus;

    private String transactionReference;

}