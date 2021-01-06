package com.tenx.fraudamlmanager.paymentsv3.api;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Niall O'Connell
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudCheckResponseV3 {

    private FraudAMLSanctionsCheckResponseCodeV3 status;
}
