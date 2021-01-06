package com.tenx.fraudamlmanager.cases.v1.domain;


import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CaseAssembler {


    public Case assembleCase(DomesticOutPayment domesticOutPayment) {

        Case outboundCase = new Case();
        outboundCase.setPrimaryPartyKey(domesticOutPayment.getPartyKey());
        outboundCase.setCaseType(Case.CaseType.FRAUD_EXCEPTION.name());

        outboundCase.add("creditorName", domesticOutPayment.getCreditorName());
        outboundCase.add("debtorName", domesticOutPayment.getDebtorName());
        outboundCase.add("messageDate", DateUtils.formatDate(domesticOutPayment.getMessageDate()));
        outboundCase.add("transactionDate", DateUtils.formatDate(domesticOutPayment.getTransactionDate()));
        outboundCase.add("transactionId", domesticOutPayment.getTransactionId());
        outboundCase.add("transactionNotes", domesticOutPayment.getTransactionNotes());
        outboundCase.add("transactionReference", domesticOutPayment.getTransactionReference());
        outboundCase.add("transactionStatus", domesticOutPayment.getTransactionStatus());

        outboundCase.add("existingPayee", domesticOutPayment.getExistingPayee());

        Optional.ofNullable(domesticOutPayment.getCreditorAccountDetails()).ifPresent(
                accountDetails -> {
            outboundCase.add("creditorAccountDetails.accountNumber",
                    accountDetails.getAccountNumber());
            outboundCase
                    .add("creditorAccountDetails.bankId", accountDetails.getBankId());
        });

        Optional.ofNullable(domesticOutPayment.getDebtorAccountDetails()).ifPresent(
                accountDetails -> {
                    outboundCase.add("debtorAccountDetails.accountNumber",
                            accountDetails.getAccountNumber());
                    outboundCase.add("debtorAccountDetails.bankId", accountDetails.getBankId());
                });


        Optional.ofNullable(domesticOutPayment.getAmount()).ifPresent(
                amount -> {
                    outboundCase.add("amount.currency", domesticOutPayment.getAmount().getCurrency());
                    outboundCase.add("amount.baseCurrency", domesticOutPayment.getAmount().getBaseCurrency());
                    outboundCase.add("amount.baseValue", domesticOutPayment.getAmount().getBaseValue());
                    outboundCase.add("amount.value", domesticOutPayment.getAmount().getValue());
                });

        return outboundCase;
    }

    public Case assembleCase(OnUsPayment onUsPayment) {

        Case onUsCase = new Case();
        onUsCase.setPrimaryPartyKey(onUsPayment.getDebtorPartyKey());
        onUsCase.setSecondaryPartyKey(onUsPayment.getCreditorPartyKey());
        onUsCase.setCaseType(Case.CaseType.FRAUD_EXCEPTION.name());

        onUsCase.add("debtorName", onUsPayment.getDebtorName());
        onUsCase.add("creditorName", onUsPayment.getCreditorName());
        onUsCase.add("creditorPartyKey", onUsPayment.getCreditorPartyKey());
        onUsCase.add("debtorPartyKey", onUsPayment.getDebtorPartyKey());
        onUsCase.add("transactionId", onUsPayment.getTransactionId());
        onUsCase.add("transactiondate", DateUtils.formatDate(onUsPayment.getTransactionDate()));
        onUsCase.add("messageDate", DateUtils.formatDate(onUsPayment.getMessageDate()));
        onUsCase.add("transactionStatus", onUsPayment.getTransactionStatus());
        onUsCase.add("transactionReference", onUsPayment.getTransactionReference());
        onUsCase.add("transactionNotes", onUsPayment.getTransactionNotes());
        onUsCase.add("existingPayee", onUsPayment.getExistingPayee());

        Optional.ofNullable(onUsPayment.getCreditorAccountDetails()).ifPresent(
                accountDetails -> {
                    onUsCase.add("creditorAccountDetails.accountNumber",
                            accountDetails.getAccountNumber());
                    onUsCase
                            .add("creditorAccountDetails.bankId", accountDetails.getBankId());
                });

        Optional.ofNullable(onUsPayment.getDebtorAccountDetails()).ifPresent(
                accountDetails -> {
                    onUsCase.add("debtorAccountDetails.accountNumber",
                            accountDetails.getAccountNumber());
                    onUsCase.add("debtorAccountDetails.bankId", accountDetails.getBankId());
                });

        if (onUsPayment.getAmount() != null) {
            onUsCase.add("amount.currency", onUsPayment.getAmount().getCurrency());
            onUsCase.add("amount.baseCurrency", onUsPayment.getAmount().getBaseCurrency());
            onUsCase.add("amount.baseValue", onUsPayment.getAmount().getBaseValue());
            onUsCase.add("amount.value", onUsPayment.getAmount().getValue());
        }

        Optional.ofNullable(onUsPayment.getAmount()).ifPresent(
                amount -> {
                    onUsCase.add("amount.currency", onUsPayment.getAmount().getCurrency());
                    onUsCase.add("amount.baseCurrency", onUsPayment.getAmount().getBaseCurrency());
                    onUsCase.add("amount.baseValue", onUsPayment.getAmount().getBaseValue());
                    onUsCase.add("amount.value", onUsPayment.getAmount().getValue());
                });
        
        return onUsCase;
    }

}