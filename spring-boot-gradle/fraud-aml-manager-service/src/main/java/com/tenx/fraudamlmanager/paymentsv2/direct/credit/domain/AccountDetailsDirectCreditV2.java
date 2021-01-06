package com.tenx.fraudamlmanager.paymentsv2.direct.credit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsDirectCreditV2 {

  private String accountNumber;

  private String bankId;

}
