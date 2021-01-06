package com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma;

import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.AccountDetailsTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.BalanceBeforeTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma.PaymentAmountTMARequestV3;
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
public class DomesticInPaymentTMARequestV3 {

    private AccountDetailsTMARequestV3 creditorAccountDetails;

    private String creditorName;

    private AccountDetailsTMARequestV3 debtorAccountDetails;

    private String debtorName;

    private PaymentAmountTMARequestV3 amount;

    private BalanceBeforeTMARequestV3 balanceBefore;

    private String transactionId;

    private Date transactionDate;

    private Date messageDate;

    private String transactionStatus;

    private String transactionReference;

    private String creditorPartyKey;
}
