package com.tenx.fraudamlmanager.cases.v2.domain;


import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CaseAssemblerV2 {


  public CaseV2 assembleCase(DomesticOutPaymentV2 domesticOutPaymentV2) {

    CaseV2 outboundCase = new CaseV2();
    outboundCase.setPrimaryPartyKey(domesticOutPaymentV2.getPartyKey());
    outboundCase.setCaseType(CaseV2.CaseType.FRAUD_EXCEPTION.name());

    outboundCase.add("creditorName", domesticOutPaymentV2.getCreditorName());
    outboundCase.add("debtorName", domesticOutPaymentV2.getDebtorName());
    outboundCase.add("messageDate", DateUtils.formatDate(domesticOutPaymentV2.getMessageDate()));
    outboundCase.add("transactionDate", DateUtils.formatDate(domesticOutPaymentV2.getTransactionDate()));
    outboundCase.add("transactionId", domesticOutPaymentV2.getTransactionId());
    outboundCase.add("transactionNotes", domesticOutPaymentV2.getTransactionNotes());
    if (domesticOutPaymentV2.getTransactionTags() != null) {
      outboundCase.add("transactionTags", String.join(", ", domesticOutPaymentV2.getTransactionTags()));
    }
    outboundCase.add("transactionReference", domesticOutPaymentV2.getTransactionReference());
    outboundCase.add("transactionStatus", domesticOutPaymentV2.getTransactionStatus());

    outboundCase.add("existingPayee", domesticOutPaymentV2.getExistingPayee());
    if (domesticOutPaymentV2.getCreditorAccountDetails() != null) {
      outboundCase.add("creditorAccountDetails.accountNumber",
        domesticOutPaymentV2.getCreditorAccountDetails().getAccountNumber());
      outboundCase.add("creditorAccountDetails.bankId", domesticOutPaymentV2.getCreditorAccountDetails().getBankId());
    }

    if (domesticOutPaymentV2.getDebtorAccountDetails() != null) {
      outboundCase
        .add("debtorAccountDetails.accountNumber", domesticOutPaymentV2.getDebtorAccountDetails().getAccountNumber());
      outboundCase.add("debtorAccountDetails.bankId", domesticOutPaymentV2.getDebtorAccountDetails().getBankId());
    }

    if (domesticOutPaymentV2.getAmount() != null) {
      outboundCase.add("amount.currency", domesticOutPaymentV2.getAmount().getCurrency());
      outboundCase.add("amount.baseCurrency", domesticOutPaymentV2.getAmount().getBaseCurrency());
      outboundCase.add("amount.baseValue", domesticOutPaymentV2.getAmount().getBaseValue());
      outboundCase.add("amount.value", domesticOutPaymentV2.getAmount().getValue());
    }

    if (domesticOutPaymentV2.getBalanceBefore() != null) {
      outboundCase.add("balanceBefore.currency", domesticOutPaymentV2.getBalanceBefore().getCurrency());
      outboundCase.add("balanceBefore.baseCurrency", domesticOutPaymentV2.getBalanceBefore().getBaseCurrency());
      outboundCase.add("balanceBefore.baseValue", domesticOutPaymentV2.getBalanceBefore().getBaseValue());
      outboundCase.add("balanceBefore.value", domesticOutPaymentV2.getBalanceBefore().getValue());
    }

    return outboundCase;
  }


  public CaseV2 assembleCase(DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2) {

    CaseV2 outboundCase = new CaseV2();
    outboundCase.setPrimaryPartyKey(domesticOutReturnPaymentV2.getPartyKey());
    outboundCase.setCaseType("FRAUD_EXCEPTION");

    outboundCase.add("creditorName", domesticOutReturnPaymentV2.getCreditorName());
    outboundCase.add("debtorName", domesticOutReturnPaymentV2.getDebtorName());

    Optional.ofNullable(domesticOutReturnPaymentV2.getMessageDate()).ifPresent(date -> outboundCase.add("messageDate", DateUtils.formatDate(date)));

    outboundCase.add("transactionDate", DateUtils.formatDate(domesticOutReturnPaymentV2.getTransactionDate()));
    outboundCase.add("transactionId", domesticOutReturnPaymentV2.getTransactionId());
    outboundCase.add("transactionNotes", domesticOutReturnPaymentV2.getTransactionNotes());

    Optional.ofNullable(domesticOutReturnPaymentV2.getTransactionTags()).ifPresent(
            tags -> outboundCase.add("transactionTags", String.join(", ", tags)));
    outboundCase.add("transactionReference", domesticOutReturnPaymentV2.getTransactionReference());
    outboundCase.add("transactionStatus", domesticOutReturnPaymentV2.getTransactionStatus());

    outboundCase.add("existingPayee", domesticOutReturnPaymentV2.getExistingPayee());

    Optional.ofNullable(domesticOutReturnPaymentV2.getCreditorAccountDetails()).ifPresent(accountDetailsV3 -> {
      outboundCase.add("creditorAccountDetails.accountNumber",
              accountDetailsV3.getAccountNumber());
      outboundCase
              .add("creditorAccountDetails.bankId", accountDetailsV3.getBankId());
    });

    Optional.ofNullable(domesticOutReturnPaymentV2.getDebtorAccountDetails()).ifPresent(
            accountDetailsV3 -> {
              outboundCase.add("debtorAccountDetails.accountNumber",
                      accountDetailsV3.getAccountNumber());
              outboundCase.add("debtorAccountDetails.bankId", accountDetailsV3.getBankId());
            });

    Optional.ofNullable(domesticOutReturnPaymentV2.getAmount()).ifPresent(amount -> {
      outboundCase.add("amount.currency", amount.getCurrency());
      outboundCase.add("amount.baseCurrency", amount.getBaseCurrency());
      outboundCase.add("amount.baseValue", amount.getBaseValue());
      outboundCase.add("amount.value", amount.getValue());
    });

    Optional.ofNullable(domesticOutReturnPaymentV2.getBalanceBefore()).ifPresent(
            balanceBefore -> {
              outboundCase.add("balanceBefore.currency", balanceBefore.getCurrency());
              outboundCase.add("balanceBefore.baseCurrency", balanceBefore.getBaseCurrency());
              outboundCase.add("balanceBefore.baseValue", balanceBefore.getBaseValue());
              outboundCase.add("balanceBefore.value", balanceBefore.getValue());
            });

    return outboundCase;
  }


  public CaseV2 assembleCase(DomesticInPaymentV2 domesticInPaymentV2) {

    CaseV2 outboundCase = new CaseV2();
    outboundCase.setPrimaryPartyKey(domesticInPaymentV2.getPartyKey());

    outboundCase.setCaseType("FRAUD_EXCEPTION");

    outboundCase.add("creditorName", domesticInPaymentV2.getCreditorName());
    outboundCase.add("debtorName", domesticInPaymentV2.getDebtorName());
    Optional.ofNullable(domesticInPaymentV2.getMessageDate()).ifPresent(date -> outboundCase.add("messageDate", DateUtils.formatDate(domesticInPaymentV2.getMessageDate())));
    outboundCase.add("transactionDate", DateUtils.formatDate(domesticInPaymentV2.getTransactionDate()));
    outboundCase.add("transactionId", domesticInPaymentV2.getTransactionId());
    outboundCase.add("transactionReference", domesticInPaymentV2.getTransactionReference());
    outboundCase.add("transactionStatus", domesticInPaymentV2.getTransactionStatus());

    Optional.ofNullable(domesticInPaymentV2.getCreditorAccountDetails()).ifPresent(accountDetailsV3 -> {
      outboundCase.add("creditorAccountDetails.accountNumber",
              accountDetailsV3.getAccountNumber());
      outboundCase
              .add("creditorAccountDetails.bankId", accountDetailsV3.getBankId());
    });

    Optional.ofNullable(domesticInPaymentV2.getDebtorAccountDetails()).ifPresent(
            accountDetailsV3 -> {
              outboundCase.add("debtorAccountDetails.accountNumber",
                      accountDetailsV3.getAccountNumber());
              outboundCase.add("debtorAccountDetails.bankId", accountDetailsV3.getBankId());
            });

    Optional.ofNullable(domesticInPaymentV2.getAmount()).ifPresent(amount -> {
      outboundCase.add("amount.currency", amount.getCurrency());
      outboundCase.add("amount.baseCurrency", amount.getBaseCurrency());
      outboundCase.add("amount.baseValue", amount.getBaseValue());
      outboundCase.add("amount.value", amount.getValue());
    });

    Optional.ofNullable(domesticInPaymentV2.getBalanceBefore()).ifPresent(
            balanceBefore -> {
              outboundCase.add("balanceBefore.currency", balanceBefore.getCurrency());
              outboundCase.add("balanceBefore.baseCurrency", balanceBefore.getBaseCurrency());
              outboundCase.add("balanceBefore.baseValue", balanceBefore.getBaseValue());
              outboundCase.add("balanceBefore.value", balanceBefore.getValue());
            });
    return outboundCase;
  }


  public CaseV2 assembleCase(OnUsPaymentV2 onUsPaymentV2) {

    CaseV2 onUsCase = new CaseV2();
    onUsCase.setPrimaryPartyKey(onUsPaymentV2.getDebtorPartyKey());
    onUsCase.setSecondaryPartyKey(onUsPaymentV2.getCreditorPartyKey());
    onUsCase.setCaseType(CaseV2.CaseType.FRAUD_EXCEPTION.name());

    onUsCase.add("debtorName", onUsPaymentV2.getDebtorName());
    onUsCase.add("creditorName", onUsPaymentV2.getCreditorName());
    onUsCase.add("creditorPartyKey", onUsPaymentV2.getCreditorPartyKey());
    onUsCase.add("debtorPartyKey", onUsPaymentV2.getDebtorPartyKey());
    onUsCase.add("transactionId", onUsPaymentV2.getTransactionId());
    onUsCase.add("transactiondate", DateUtils.formatDate(onUsPaymentV2.getTransactionDate()));
    onUsCase.add("messageDate", DateUtils.formatDate(onUsPaymentV2.getMessageDate()));
    onUsCase.add("transactionStatus", onUsPaymentV2.getTransactionStatus());
    onUsCase.add("transactionReference", onUsPaymentV2.getTransactionReference());
    if (onUsPaymentV2.getTransactionTags() != null) {
      onUsCase.add("transactionTags", String.join(", ", onUsPaymentV2.getTransactionTags()));
    }
    onUsCase.add("transactionNotes", onUsPaymentV2.getTransactionNotes());
    onUsCase.add("existingPayee", onUsPaymentV2.getExistingPayee());

    if (onUsPaymentV2.getCreditorAccountDetails() != null) {
      onUsCase
        .add("creditorAccountDetails.accountNumber", onUsPaymentV2.getCreditorAccountDetails().getAccountNumber());
      onUsCase.add("creditorAccountDetails.bankId", onUsPaymentV2.getCreditorAccountDetails().getBankId());
    }

    if (onUsPaymentV2.getDebtorAccountDetails() != null) {
      onUsCase.add("debtorAccountDetails.accountNumber", onUsPaymentV2.getDebtorAccountDetails().getAccountNumber());
      onUsCase.add("debtorAccountDetails.bankId", onUsPaymentV2.getDebtorAccountDetails().getBankId());
    }

    if (onUsPaymentV2.getAmount() != null) {
      onUsCase.add("amount.currency", onUsPaymentV2.getAmount().getCurrency());
      onUsCase.add("amount.baseCurrency", onUsPaymentV2.getAmount().getBaseCurrency());
      onUsCase.add("amount.baseValue", onUsPaymentV2.getAmount().getBaseValue());
      onUsCase.add("amount.value", onUsPaymentV2.getAmount().getValue());
    }

    if (onUsPaymentV2.getBalanceBefore() != null) {
      onUsCase.add("balanceBefore.currency", onUsPaymentV2.getBalanceBefore().getCurrency());
      onUsCase.add("balanceBefore.baseCurrency", onUsPaymentV2.getBalanceBefore().getBaseCurrency());
      onUsCase.add("balanceBefore.baseValue", onUsPaymentV2.getBalanceBefore().getBaseValue());
      onUsCase.add("balanceBefore.value", onUsPaymentV2.getBalanceBefore().getValue());
    }

    return onUsCase;
  }


}
