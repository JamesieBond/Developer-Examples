package com.tenx.fraudamlmanager.paymentsv3.onus.cases.domain;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseHandlerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OnUsCaseServiceImplV3 implements OnUsCaseServiceV3 {

	private final OnUsAssemblerV3 caseAssembler;
	private final CaseHandlerV2 caseHandlerV2;

	@Override
	public void processCaseForOnUs(FraudAMLSanctionsCheckResponseCodeV3 status, OnUsPaymentV3 onUsPaymentV3) {
		CaseV2 caseV2 = caseAssembler.assembleCase(onUsPaymentV3);
		caseHandlerV2.processPaymentCase(status, onUsPaymentV3.getTransactionId(),
				onUsPaymentV3.getClass().getSimpleName(),
				caseV2);
	}
}


