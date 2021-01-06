package com.tenx.fraudamlmanager.paymentsv3.direct.credit.cases.domain;

import com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain.DirectCreditPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;

public interface DirectCreditCaseServiceV3 {

    void processPaymentForCase(FraudAMLSanctionsCheckResponseCodeV3 status, DirectCreditPaymentV3 directCreditPaymentV3);

}
