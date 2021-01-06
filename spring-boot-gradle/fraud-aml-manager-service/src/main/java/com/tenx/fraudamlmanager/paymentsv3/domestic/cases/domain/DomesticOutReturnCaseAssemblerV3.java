package com.tenx.fraudamlmanager.paymentsv3.domestic.cases.domain;


import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
import java.util.Optional;
import org.springframework.stereotype.Component;


@Component
class DomesticOutReturnCaseAssemblerV3 {

  public CaseV2 assembleCase(DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3) {

    CaseV2 outboundCase = new CaseV2();
    outboundCase.setPrimaryPartyKey(domesticOutReturnPaymentV3.getPartyKey());
    outboundCase.setCaseType("FRAUD_EXCEPTION");

        outboundCase.add("creditorName", domesticOutReturnPaymentV3.getCreditorName());
        outboundCase.add("debtorName", domesticOutReturnPaymentV3.getDebtorName());

        Optional.ofNullable(domesticOutReturnPaymentV3.getMessageDate()).ifPresent(date -> outboundCase.add("messageDate", DateUtils.formatDate(date)));

        outboundCase.add("transactionDate", DateUtils.formatDate(domesticOutReturnPaymentV3.getTransactionDate()));
        outboundCase.add("transactionId", domesticOutReturnPaymentV3.getTransactionId());
        outboundCase.add("transactionNotes", domesticOutReturnPaymentV3.getTransactionNotes());

        Optional.ofNullable(domesticOutReturnPaymentV3.getTransactionTags()).ifPresent(
                tags -> outboundCase.add("transactionTags", String.join(", ", tags)));
        outboundCase.add("transactionReference", domesticOutReturnPaymentV3.getTransactionReference());
        outboundCase.add("transactionStatus", domesticOutReturnPaymentV3.getTransactionStatus());

        outboundCase.add("existingPayee", domesticOutReturnPaymentV3.getExistingPayee());

        Optional.ofNullable(domesticOutReturnPaymentV3.getCreditorAccountDetails()).ifPresent(accountDetailsV3 -> {
            outboundCase.add("creditorAccountDetails.accountNumber",
                    accountDetailsV3.getAccountNumber());
            outboundCase
                    .add("creditorAccountDetails.bankId", accountDetailsV3.getBankId());
        });

        Optional.ofNullable(domesticOutReturnPaymentV3.getDebtorAccountDetails()).ifPresent(
                accountDetailsV3 -> {
                    outboundCase.add("debtorAccountDetails.accountNumber",
                            accountDetailsV3.getAccountNumber());
                    outboundCase.add("debtorAccountDetails.bankId", accountDetailsV3.getBankId());
                });

        Optional.ofNullable(domesticOutReturnPaymentV3.getAmount()).ifPresent(amount -> {
            outboundCase.add("amount.currency", amount.getCurrency());
            outboundCase.add("amount.baseCurrency", amount.getBaseCurrency());
            outboundCase.add("amount.baseValue", amount.getBaseValue());
            outboundCase.add("amount.value", amount.getValue());
        });

        Optional.ofNullable(domesticOutReturnPaymentV3.getBalanceBefore()).ifPresent(
                balanceBefore -> {
                    outboundCase.add("balanceBefore.currency", balanceBefore.getCurrency());
                    outboundCase.add("balanceBefore.baseCurrency", balanceBefore.getBaseCurrency());
                    outboundCase.add("balanceBefore.baseValue", balanceBefore.getBaseValue());
                    outboundCase.add("balanceBefore.value", balanceBefore.getValue());
                });

        return outboundCase;
    }


}
