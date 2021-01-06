package com.tenx.fraudamlmanager.paymentsv3.direct.debit.infrastructure.tma;

import com.tenx.fraudamlmanager.paymentsv3.domain.AccountDetailsV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.BalanceBeforeV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.PaymentAmountV3;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectDebitPaymentV3TMARequest {

    @Valid
    @NotNull
    private AccountDetailsV3 creditorAccountDetails;

    @NotEmpty
    private String creditorName;

    @Valid
    @NotNull
    private AccountDetailsV3 debtorAccountDetails;

    @NotEmpty
    private String debtorName;

    @Valid
    @NotNull
    private PaymentAmountV3 amount;

    private String partyKey;

    @NotEmpty
    private String transactionId;

    @NotNull
    private Date transactionDate;

    private Date messageDate;

    @Valid
    @NotNull
    private BalanceBeforeV3 balanceBefore;

    private String transactionStatus;

    private String transactionReference;

}
