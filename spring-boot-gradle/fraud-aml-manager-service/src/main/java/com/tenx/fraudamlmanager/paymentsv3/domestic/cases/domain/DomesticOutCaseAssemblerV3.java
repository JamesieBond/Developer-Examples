package com.tenx.fraudamlmanager.paymentsv3.domestic.cases.domain;


import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import java.util.Optional;
import org.springframework.stereotype.Component;


@Component
class DomesticOutCaseAssemblerV3 {

  public CaseV2 assembleCase(DomesticOutPaymentV3 domesticOutPaymentV3) {

    CaseV2 outboundCase = new CaseV2();
    outboundCase.setPrimaryPartyKey(domesticOutPaymentV3.getDebtorPartyKey());
    outboundCase.setCaseType("FRAUD_EXCEPTION");

        outboundCase.add("creditorName", domesticOutPaymentV3.getCreditorName());
        outboundCase.add("debtorName", domesticOutPaymentV3.getDebtorName());
        Optional.ofNullable(domesticOutPaymentV3.getMessageDate()).ifPresent(date -> outboundCase.add("messageDate", DateUtils.formatDate(date)));
        outboundCase.add("transactionDate", DateUtils.formatDate(domesticOutPaymentV3.getTransactionDate()));
        outboundCase.add("transactionId", domesticOutPaymentV3.getTransactionId());
        outboundCase.add("transactionNotes", domesticOutPaymentV3.getTransactionNotes());
        Optional.ofNullable(domesticOutPaymentV3.getTransactionTags()).ifPresent(tags -> outboundCase.add("transactionTags", String.join(", ", tags)));
        outboundCase.add("transactionReference", domesticOutPaymentV3.getTransactionReference());
        outboundCase.add("transactionStatus", domesticOutPaymentV3.getTransactionStatus());

        outboundCase.add("existingPayee", domesticOutPaymentV3.getExistingPayee());
        Optional.ofNullable(domesticOutPaymentV3.getCreditorAccountDetails()).ifPresent(accountDetailsV3 -> {
            outboundCase.add("creditorAccountDetails.accountNumber",
                    accountDetailsV3.getAccountNumber());
            outboundCase
                    .add("creditorAccountDetails.bankId", accountDetailsV3.getBankId());
        });

        Optional.ofNullable(domesticOutPaymentV3.getDebtorAccountDetails()).ifPresent(
                accountDetailsV3 -> {
                    outboundCase.add("debtorAccountDetails.accountNumber",
                            accountDetailsV3.getAccountNumber());
                    outboundCase.add("debtorAccountDetails.bankId", accountDetailsV3.getBankId());
                });

        Optional.ofNullable(domesticOutPaymentV3.getAmount()).ifPresent(amount -> {
            outboundCase.add("amount.currency", amount.getCurrency());
            outboundCase.add("amount.baseCurrency", amount.getBaseCurrency());
            outboundCase.add("amount.baseValue", amount.getBaseValue());
            outboundCase.add("amount.value", amount.getValue());
        });

        Optional.ofNullable(domesticOutPaymentV3.getBalanceBefore()).ifPresent(
                balanceBefore -> {
                    outboundCase.add("balanceBefore.currency", balanceBefore.getCurrency());
                    outboundCase.add("balanceBefore.baseCurrency", balanceBefore.getBaseCurrency());
                    outboundCase.add("balanceBefore.baseValue", balanceBefore.getBaseValue());
                    outboundCase.add("balanceBefore.value", balanceBefore.getValue());
                });

        return outboundCase;
    }

}
