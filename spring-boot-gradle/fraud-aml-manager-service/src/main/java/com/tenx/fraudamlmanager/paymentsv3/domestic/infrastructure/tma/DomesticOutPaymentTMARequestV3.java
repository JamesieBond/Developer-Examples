package com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma;

import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.AccountDetailsTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.BalanceBeforeTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.PaymentAmountTMARequestV3;
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
public class DomesticOutPaymentTMARequestV3 {

    private AccountDetailsTMARequestV3 creditorAccountDetails;

    private String creditorName;

    private AccountDetailsTMARequestV3 debtorAccountDetails;

    private String debtorName;

    private String debtorPartyKey;

    private PaymentAmountTMARequestV3 amount;

    private String transactionId;

    private Date transactionDate;

    private BalanceBeforeTMARequestV3 balanceBefore;

    private Date messageDate;

    private String transactionStatus;

    private String transactionReference;

    private String transactionNotes;

    private List<String> transactionTags;

    private Boolean existingPayee;

    private HashMap<String, Object> threatmetrixData;

}
