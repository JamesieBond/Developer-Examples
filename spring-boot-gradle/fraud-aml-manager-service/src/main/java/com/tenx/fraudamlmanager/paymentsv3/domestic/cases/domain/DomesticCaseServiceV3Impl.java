package com.tenx.fraudamlmanager.paymentsv3.domestic.cases.domain;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseHandlerV2;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class DomesticCaseServiceV3Impl implements DomesticCaseServiceV3 {

    private final DomesticInCaseAssemblerV3 domesticInCaseAssemblerV3;
    private final DomesticOutCaseAssemblerV3 domesticOutCaseAssemblerV3;
    private final DomesticOutReturnCaseAssemblerV3 domesticOuReturnCaseAssemblerV3;
    private final CaseHandlerV2 caseHandlerV2;

    @Override
    public void processCaseForDomesticIn(FraudAMLSanctionsCheckResponseCodeV3 status, DomesticInPaymentV3 domesticInPaymentV3) {
        caseHandlerV2.processPaymentCase(status, domesticInPaymentV3.getTransactionId(), domesticInPaymentV3.getClass().getSimpleName(),
            domesticInCaseAssemblerV3.assembleCase(domesticInPaymentV3));
    }

    @Override
    public void processCaseForDomesticOut(FraudAMLSanctionsCheckResponseCodeV3 status, DomesticOutPaymentV3 domesticOutPaymentV3) {
        caseHandlerV2.processPaymentCase(status, domesticOutPaymentV3.getTransactionId(), domesticOutPaymentV3.getClass().getSimpleName(),
            domesticOutCaseAssemblerV3.assembleCase(domesticOutPaymentV3));
    }

    @Override
    public void processCaseForDomesticOutReturn(FraudAMLSanctionsCheckResponseCodeV3 status, DomesticOutReturnPaymentV3 domesticOutReturnPaymentV3) {
        caseHandlerV2.processPaymentCase(status, domesticOutReturnPaymentV3.getTransactionId(), domesticOutReturnPaymentV3.getClass().getSimpleName(),
            domesticOuReturnCaseAssemblerV3.assembleCase(domesticOutReturnPaymentV3));
    }

}


