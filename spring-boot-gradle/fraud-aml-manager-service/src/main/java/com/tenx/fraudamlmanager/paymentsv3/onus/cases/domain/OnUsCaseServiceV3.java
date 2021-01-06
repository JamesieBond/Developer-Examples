package com.tenx.fraudamlmanager.paymentsv3.onus.cases.domain;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;

public interface OnUsCaseServiceV3 {
    void processCaseForOnUs(FraudAMLSanctionsCheckResponseCodeV3 status, OnUsPaymentV3 onUsPaymentV3);
}
