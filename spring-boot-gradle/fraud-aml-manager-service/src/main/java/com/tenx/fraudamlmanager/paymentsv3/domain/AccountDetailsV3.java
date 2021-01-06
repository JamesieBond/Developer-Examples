package com.tenx.fraudamlmanager.paymentsv3.domain;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsV3 {

    @NotEmpty
    private String accountNumber;

    @NotEmpty
    private String bankId;

}
