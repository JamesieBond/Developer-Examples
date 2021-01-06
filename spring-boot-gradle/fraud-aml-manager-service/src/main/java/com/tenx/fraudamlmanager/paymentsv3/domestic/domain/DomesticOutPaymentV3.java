package com.tenx.fraudamlmanager.paymentsv3.domestic.domain;

import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomesticOutPaymentV3 {

    private AccountDetailsV3 creditorAccountDetails;

    private String creditorName;

    private AccountDetailsV3 debtorAccountDetails;

    private String debtorName;

    private String debtorPartyKey;

    private PaymentAmountV3 amount;

    private String transactionId;

    private Date transactionDate;

    private BalanceBeforeV3 balanceBefore;

    private Date messageDate;

    private String transactionStatus;

    private String transactionReference;

    private String transactionNotes;

    private List<String> transactionTags;

    private Boolean existingPayee;

    private HashMap<String, Object> threatmetrixData;

}
