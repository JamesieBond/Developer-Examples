package com.tenx.fraudamlmanager.paymentsv2.onus.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsOnUsV2 {

  private String accountNumber;

  private String bankId;

}
