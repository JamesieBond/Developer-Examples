package com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases.domain;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseHandlerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class DirectDebitCaseServiceImplV3 implements DirectDebitCaseServiceV3 {

	@Autowired
	private CaseHandlerV2 caseHandlerV2;
	@Autowired
	private DirectDebitCaseAssemblerV3 caseAssembler;


	@Override
	public void processCaseForDirectDebit(FraudAMLSanctionsCheckResponseCodeV3 status, DirectDebitPaymentV3 directDebitPaymentV3) {
		CaseV2 caseV2 = caseAssembler.assembleCase(directDebitPaymentV3);
		caseHandlerV2.processPaymentCase(status, directDebitPaymentV3.getTransactionId(), directDebitPaymentV3.getClass().getSimpleName(), caseV2);
	}
}


