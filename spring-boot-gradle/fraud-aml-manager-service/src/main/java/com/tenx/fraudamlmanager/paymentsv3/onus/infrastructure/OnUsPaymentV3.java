package com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure;


import com.tenx.fraudamlmanager.paymentsv3.api.AccountDetailsRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.BalanceBeforeRequestV3;
import com.tenx.fraudamlmanager.paymentsv3.api.PaymentAmountRequestV3;
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
public class OnUsPaymentV3 {

    private AccountDetailsRequestV3 creditorAccountDetails;

    private String creditorName;

    private AccountDetailsRequestV3 debtorAccountDetails;

    private String debtorName;

    private PaymentAmountRequestV3 amount;

    private BalanceBeforeRequestV3 balanceBefore;

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

    private HashMap<String, Object> threatmetrixData;

}
