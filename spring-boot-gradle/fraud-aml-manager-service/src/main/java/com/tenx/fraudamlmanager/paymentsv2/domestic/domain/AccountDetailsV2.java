package com.tenx.fraudamlmanager.paymentsv2.domestic.domain;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsV2 {

    private String accountName;

    @NotEmpty
    private String accountNumber;

    private String accountAddress;

    @NotEmpty
    private String bankId;

    private String financialInstitutionIdentification;

    private String iban;

}
