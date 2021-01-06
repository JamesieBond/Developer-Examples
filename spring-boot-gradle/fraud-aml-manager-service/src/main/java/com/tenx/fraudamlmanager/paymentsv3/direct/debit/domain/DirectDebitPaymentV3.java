package com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain;

import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectDebitPaymentV3 {

	private AccountDetailsV3 creditorAccountDetails;

	private String creditorName;

	private AccountDetailsV3 debtorAccountDetails;

	private String debtorName;

	private PaymentAmountV3 amount;

	private String partyKey;

	private String transactionId;

	private Date transactionDate;

	private Date messageDate;

	private BalanceBeforeV3 balanceBefore;

	private String transactionStatus;

	private String transactionReference;

}
