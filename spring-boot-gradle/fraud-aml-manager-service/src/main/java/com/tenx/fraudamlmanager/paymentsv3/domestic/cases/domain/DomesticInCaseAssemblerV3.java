package com.tenx.fraudamlmanager.paymentsv3.domestic.cases.domain;


import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import java.util.Optional;
import org.springframework.stereotype.Component;


@Component
class DomesticInCaseAssemblerV3 {

  public CaseV2 assembleCase(DomesticInPaymentV3 domesticInPaymentV3) {

    CaseV2 outboundCase = new CaseV2();
    outboundCase.setPrimaryPartyKey(domesticInPaymentV3.getCreditorPartyKey());

        outboundCase.setCaseType("FRAUD_EXCEPTION");

        outboundCase.add("creditorName", domesticInPaymentV3.getCreditorName());
        outboundCase.add("debtorName", domesticInPaymentV3.getDebtorName());
        Optional.ofNullable(domesticInPaymentV3.getMessageDate()).ifPresent(date -> outboundCase.add("messageDate", DateUtils.formatDate(domesticInPaymentV3.getMessageDate())));
        outboundCase.add("transactionDate", DateUtils.formatDate(domesticInPaymentV3.getTransactionDate()));
        outboundCase.add("transactionId", domesticInPaymentV3.getTransactionId());
        outboundCase.add("transactionReference", domesticInPaymentV3.getTransactionReference());
        outboundCase.add("transactionStatus", domesticInPaymentV3.getTransactionStatus());

        Optional.ofNullable(domesticInPaymentV3.getCreditorAccountDetails()).ifPresent(accountDetailsV3 -> {
            outboundCase.add("creditorAccountDetails.accountNumber",
                    accountDetailsV3.getAccountNumber());
            outboundCase
                    .add("creditorAccountDetails.bankId", accountDetailsV3.getBankId());
        });

        Optional.ofNullable(domesticInPaymentV3.getDebtorAccountDetails()).ifPresent(
                accountDetailsV3 -> {
                    outboundCase.add("debtorAccountDetails.accountNumber",
                            accountDetailsV3.getAccountNumber());
                    outboundCase.add("debtorAccountDetails.bankId", accountDetailsV3.getBankId());
                });

        Optional.ofNullable(domesticInPaymentV3.getAmount()).ifPresent(amount -> {
            outboundCase.add("amount.currency", amount.getCurrency());
            outboundCase.add("amount.baseCurrency", amount.getBaseCurrency());
            outboundCase.add("amount.baseValue", amount.getBaseValue());
            outboundCase.add("amount.value", amount.getValue());
        });

        Optional.ofNullable(domesticInPaymentV3.getBalanceBefore()).ifPresent(
                balanceBefore -> {
                    outboundCase.add("balanceBefore.currency", balanceBefore.getCurrency());
                    outboundCase.add("balanceBefore.baseCurrency", balanceBefore.getBaseCurrency());
                    outboundCase.add("balanceBefore.baseValue", balanceBefore.getBaseValue());
                    outboundCase.add("balanceBefore.value", balanceBefore.getValue());
                });
        return outboundCase;
    }

}
