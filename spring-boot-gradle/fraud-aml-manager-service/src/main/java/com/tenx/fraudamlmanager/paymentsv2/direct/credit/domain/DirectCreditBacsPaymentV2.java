package com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectCreditBacsPaymentV2 {

  private AccountDetailsDirectCreditV2 creditorAccountDetails;

  private String creditorName;

  private AccountDetailsDirectCreditV2 debtorAccountDetails;

  private String debtorName;

  private PaymentAmountDirectCreditV2 amount;

  private String partyKey;

  private String transactionId;

  private Date transactionDate;

  private Date messageDate;

  private String transactionStatus;

  private String transactionReference;

}
