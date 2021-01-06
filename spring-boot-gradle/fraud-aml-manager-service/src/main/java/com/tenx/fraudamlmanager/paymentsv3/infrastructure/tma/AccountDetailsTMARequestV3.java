package com.tenx.fraudamlmanager.paymentsv3.infrastructure.tma;

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
public class AccountDetailsTMARequestV3 {

    @NotEmpty
    private String accountNumber;

    @NotEmpty
    private String bankId;

}
