package com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases.domain;


import com.tenx.fraudamlmanager.application.DateUtils;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitPaymentV3;
import java.util.Optional;
import org.springframework.stereotype.Component;


@Component
public class DirectDebitCaseAssemblerV3 {

	public CaseV2 assembleCase(DirectDebitPaymentV3 directDebitPaymentV3) {

		CaseV2 directDebitCase = new CaseV2();
		directDebitCase.setPrimaryPartyKey(directDebitPaymentV3.getPartyKey());

		directDebitCase.setCaseType("FRAUD_EXCEPTION");

    directDebitCase.add("debtorName", directDebitPaymentV3.getDebtorName());
    directDebitCase.add("creditorName", directDebitPaymentV3.getCreditorName());
    directDebitCase.add("partyKey", directDebitPaymentV3.getPartyKey());
    directDebitCase.add("transactionId", directDebitPaymentV3.getTransactionId());
    directDebitCase.add("transactiondate", DateUtils.formatDate(directDebitPaymentV3.getTransactionDate()));
    directDebitCase.add("messageDate", DateUtils.formatDate(directDebitPaymentV3.getMessageDate()));
    directDebitCase.add("transactionStatus", directDebitPaymentV3.getTransactionStatus());
    directDebitCase.add("transactionReference", directDebitPaymentV3.getTransactionReference());

    Optional.ofNullable(directDebitPaymentV3.getCreditorAccountDetails()).ifPresent(accountDetailsV3 -> {
      directDebitCase.add("creditorAccountDetails.accountNumber",
              accountDetailsV3.getAccountNumber());
      directDebitCase
              .add("creditorAccountDetails.bankId", accountDetailsV3.getBankId());
    });

    Optional.ofNullable(directDebitPaymentV3.getDebtorAccountDetails()).ifPresent(accountDetailsV3 -> {
      directDebitCase.add("debtorAccountDetails.accountNumber",
              accountDetailsV3.getAccountNumber());
      directDebitCase
              .add("debtorAccountDetails.bankId", accountDetailsV3.getBankId());
    });

    Optional.ofNullable(directDebitPaymentV3.getAmount()).ifPresent(paymentAmountV3 -> {
      directDebitCase.add("amount.currency", paymentAmountV3.getCurrency());
      directDebitCase.add("amount.baseCurrency", paymentAmountV3.getBaseCurrency());
      directDebitCase.add("amount.baseValue", paymentAmountV3.getBaseValue());
      directDebitCase.add("amount.value", paymentAmountV3.getValue());
    });

    Optional.ofNullable(directDebitPaymentV3.getBalanceBefore()).ifPresent(balanceBeforeV3 -> {
      directDebitCase.add("balanceBefore.currency", balanceBeforeV3.getCurrency());
      directDebitCase.add("balanceBefore.baseCurrency", balanceBeforeV3.getBaseCurrency());
      directDebitCase.add("balanceBefore.baseValue", balanceBeforeV3.getBaseValue());
      directDebitCase.add("balanceBefore.value", balanceBeforeV3.getValue());
    });

    return directDebitCase;
  }

}
