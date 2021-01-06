package com.tenx.fraudamlmanager.paymentsv2.onus.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO object for on-Us payments.
 *
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnUsPaymentV2 {

  private AccountDetailsOnUsV2 creditorAccountDetails;

  private String creditorName;

  private AccountDetailsOnUsV2 debtorAccountDetails;

  private String debtorName;

  private PaymentAmountOnUsV2 amount;

  private BalanceBeforeOnUsV2 balanceBefore;

  private String creditorPartyKey;

  private String debtorPartyKey;

  private String transactionId;

  private Date transactionDate;

  private Date messageDate;

  private String transactionStatus;

  private String transactionReference;

  private String transactionNotes;

  private List<String> transactionTags;

  private Boolean existingPayee;

  private HashMap<String, String> threatmetrixData;

}
