package com.tenx.fraudamlmanager.cases.v2.domain;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;

public interface CaseHandlerV2 {
    void processPaymentCase(FraudAMLSanctionsCheckResponseCodeV3 status, String transactionId, String paymentType, CaseV2 caseV2);
}
