package com.tenx.fraudamlmanager.paymentsv3.domestic.cases.domain;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;

public interface DomesticCaseServiceV3 {

    void processCaseForDomesticIn(FraudAMLSanctionsCheckResponseCodeV3 status, DomesticInPaymentV3 domesticInPaymentV3);

    void processCaseForDomesticOut(FraudAMLSanctionsCheckResponseCodeV3 status, DomesticOutPaymentV3 domesticOutPaymentV3);

    void processCaseForDomesticOutReturn(FraudAMLSanctionsCheckResponseCodeV3 status, DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3);

}
