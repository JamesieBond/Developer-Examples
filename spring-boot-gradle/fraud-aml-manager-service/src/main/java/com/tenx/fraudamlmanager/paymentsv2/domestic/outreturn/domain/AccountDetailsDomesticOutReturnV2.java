package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsDomesticOutReturnV2 {

  private String accountNumber;

  private String bankId;

}
