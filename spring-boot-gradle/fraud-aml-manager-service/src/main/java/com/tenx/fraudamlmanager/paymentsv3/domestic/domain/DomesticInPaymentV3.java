package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO object for DomesticIn payments.
 *
 * @author Niall O'Connell
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomesticInPaymentV3 {

	private AccountDetailsV3 creditorAccountDetails;

	private String creditorName;

	private AccountDetailsV3 debtorAccountDetails;

	private String debtorName;

	private PaymentAmountV3 amount;

	private BalanceBeforeV3 balanceBefore;

	private String transactionId;

	private Date transactionDate;


	private Date messageDate;

	private String transactionStatus;

	private String transactionReference;

	private String creditorPartyKey;
}
