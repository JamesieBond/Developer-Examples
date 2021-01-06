package com.tenx.fraudamlmanager.paymentsv3.direct.credit.cases.domain;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseHandlerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.direct.credit.domain.DirectCreditPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class DirectCreditCaseServiceImplV3 implements DirectCreditCaseServiceV3 {

    @Autowired
    private CaseHandlerV2 caseHandlerV2;
    @Autowired
    private DirectCreditCaseAssemblerV3 caseAssembler;

    private CaseV2 assembleCase(DirectCreditPaymentV3 directCreditPaymentV3) {
        return caseAssembler.assembleCase(directCreditPaymentV3);
    }

    @Override
    public void processPaymentForCase(FraudAMLSanctionsCheckResponseCodeV3 status, DirectCreditPaymentV3 directCreditPaymentV3) {
        CaseV2 caseV2 = caseAssembler.assembleCase(directCreditPaymentV3);
        caseHandlerV2.processPaymentCase(status, directCreditPaymentV3.getTransactionId(), directCreditPaymentV3.getClass().getSimpleName(), caseV2);
    }
}


