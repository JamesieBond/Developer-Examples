package com.tenx.fraudamlmanager.paymentsv3.onus.cases.domain;


import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import java.util.Optional;
import org.springframework.stereotype.Component;


@Component
public class OnUsAssemblerV3 {

    public CaseV2 assembleCase(OnUsPaymentV3 onUsPaymentV3) {

        CaseV2 onUsCase = new CaseV2();
        onUsCase.setPrimaryPartyKey(onUsPaymentV3.getDebtorPartyKey());
        onUsCase.setSecondaryPartyKey(onUsPaymentV3.getCreditorPartyKey());

        onUsCase.setCaseType("FRAUD_EXCEPTION");

        onUsCase.add("debtorName", onUsPaymentV3.getDebtorName());
        onUsCase.add("creditorName", onUsPaymentV3.getCreditorName());
        onUsCase.add("creditorPartyKey", onUsPaymentV3.getCreditorPartyKey());
        onUsCase.add("debtorPartyKey", onUsPaymentV3.getDebtorPartyKey());
        onUsCase.add("transactionId", onUsPaymentV3.getTransactionId());
        onUsCase.add("transactiondate", DateUtils.formatDate(onUsPaymentV3.getTransactionDate()));
        Optional.ofNullable(onUsPaymentV3.getMessageDate()).ifPresent(date -> onUsCase.add("messageDate", DateUtils.formatDate(date)));
        onUsCase.add("transactionStatus", onUsPaymentV3.getTransactionStatus());
        onUsCase.add("transactionReference", onUsPaymentV3.getTransactionReference());
        Optional.of(onUsPaymentV3.getTransactionTags()).ifPresent(tags -> onUsCase.add("transactionTags", String.join(", ", tags)));
        onUsCase.add("transactionNotes", onUsPaymentV3.getTransactionNotes());
        onUsCase.add("existingPayee", onUsPaymentV3.getExistingPayee());

        Optional.ofNullable(onUsPaymentV3.getCreditorAccountDetails()).ifPresent(accountDetailsV3 -> {
            onUsCase.add("creditorAccountDetails.accountNumber",
                    accountDetailsV3.getAccountNumber());
            onUsCase
                    .add("creditorAccountDetails.bankId", accountDetailsV3.getBankId());
        });

        Optional.ofNullable(onUsPaymentV3.getDebtorAccountDetails()).ifPresent(
                accountDetailsV3 -> {
                    onUsCase.add("debtorAccountDetails.accountNumber",
                            accountDetailsV3.getAccountNumber());
                    onUsCase.add("debtorAccountDetails.bankId", accountDetailsV3.getBankId());
                });

        Optional.ofNullable(onUsPaymentV3.getAmount()).ifPresent(amount -> {
            onUsCase.add("amount.currency", amount.getCurrency());
            onUsCase.add("amount.baseCurrency", amount.getBaseCurrency());
            onUsCase.add("amount.baseValue", amount.getBaseValue());
            onUsCase.add("amount.value", amount.getValue());
        });

        Optional.ofNullable(onUsPaymentV3.getBalanceBefore()).ifPresent(
                balanceBefore -> {
                    onUsCase.add("balanceBefore.currency", balanceBefore.getCurrency());
                    onUsCase.add("balanceBefore.baseCurrency", balanceBefore.getBaseCurrency());
                    onUsCase.add("balanceBefore.baseValue", balanceBefore.getBaseValue());
                    onUsCase.add("balanceBefore.value", balanceBefore.getValue());
                });

        return onUsCase;
    }

}
