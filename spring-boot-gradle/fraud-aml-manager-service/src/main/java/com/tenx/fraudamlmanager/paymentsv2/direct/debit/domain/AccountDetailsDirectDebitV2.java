package com.tenx.fraudamlmanager.paymentsv2.direct.debit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsDirectDebitV2 {

    private String accountNumber;

    private String bankId;

}
