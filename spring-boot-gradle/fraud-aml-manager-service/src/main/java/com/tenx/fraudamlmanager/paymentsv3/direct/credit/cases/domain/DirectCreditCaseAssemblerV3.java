package com.tenx.fraudamlmanager.paymentsv3.direct.credit.cases.domain;


import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain.DirectCreditPaymentV3;
import java.util.Optional;
import org.springframework.stereotype.Component;


@Component
public class DirectCreditCaseAssemblerV3 {

    public CaseV2 assembleCase(DirectCreditPaymentV3 directCreditPaymentV3) {

        CaseV2 directCreditCase = new CaseV2();
        directCreditCase.setPrimaryPartyKey(directCreditPaymentV3.getPartyKey());

        directCreditCase.setCaseType("FRAUD_EXCEPTION");

        directCreditCase.add("debtorName", directCreditPaymentV3.getDebtorName());
        directCreditCase.add("creditorName", directCreditPaymentV3.getCreditorName());
        directCreditCase.add("partyKey", directCreditPaymentV3.getPartyKey());
        directCreditCase.add("transactionId", directCreditPaymentV3.getTransactionId());
        directCreditCase.add("transactiondate", DateUtils.formatDate(directCreditPaymentV3.getTransactionDate()));
        directCreditCase.add("messageDate", DateUtils.formatDate(directCreditPaymentV3.getMessageDate()));
        directCreditCase.add("transactionStatus", directCreditPaymentV3.getTransactionStatus());
        directCreditCase.add("transactionReference", directCreditPaymentV3.getTransactionReference());

        Optional.ofNullable(directCreditPaymentV3.getCreditorAccountDetails())
                .ifPresent(accountDetailsV3 -> {
                    directCreditCase.add("creditorAccountDetails.accountNumber",
                            accountDetailsV3.getAccountNumber());
                    directCreditCase
                            .add("creditorAccountDetails.bankId", accountDetailsV3.getBankId());
                });

        Optional.ofNullable(directCreditPaymentV3.getDebtorAccountDetails())
                .ifPresent(accountDetailsV3 -> {
                    directCreditCase
                            .add("debtorAccountDetails.accountNumber",
                                    accountDetailsV3.getAccountNumber());
                    directCreditCase
                            .add("debtorAccountDetails.bankId", accountDetailsV3.getBankId());
                });

        Optional.ofNullable(directCreditPaymentV3.getAmount())
                .ifPresent(paymentAmountV3 -> {
                    directCreditCase.add("amount.currency", paymentAmountV3.getCurrency());
                    directCreditCase.add("amount.baseCurrency", paymentAmountV3.getBaseCurrency());
                    directCreditCase.add("amount.baseValue", paymentAmountV3.getBaseValue());
                    directCreditCase.add("amount.value", paymentAmountV3.getValue());
                });

        Optional.ofNullable(directCreditPaymentV3.getBalanceBefore())
                .ifPresent(balanceBeforeV3 -> {
                    directCreditCase.add("balanceBefore.currency", balanceBeforeV3.getCurrency());
                    directCreditCase
                            .add("balanceBefore.baseCurrency", balanceBeforeV3.getBaseCurrency());
                    directCreditCase.add("balanceBefore.baseValue", balanceBeforeV3.getBaseValue());
                    directCreditCase.add("balanceBefore.value", balanceBeforeV3.getValue());
                });

        return directCreditCase;
    }

}
