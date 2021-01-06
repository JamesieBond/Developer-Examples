package com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases.domain;

import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;

public interface DirectDebitCaseServiceV3 {

    void processCaseForDirectDebit(FraudAMLSanctionsCheckResponseCodeV3 status, DirectDebitPaymentV3 directCreditPaymentV3);

}
