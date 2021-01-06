package com.tenx.fraudamlmanager.cases.v2.domain;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CaseHandlerV2Impl implements CaseHandlerV2 {

	private final PaymentCaseDataStore paymentCaseDataStore;
    private final CaseGovernorConnectorV2 caseGovernorConnectorV2;

	public void processPaymentCase(FraudAMLSanctionsCheckResponseCodeV3 status, String transactionId, String paymentType, CaseV2 caseV2) {
		switch (status) {
			case pending:
				paymentCaseDataStore.createCase(transactionId, paymentType, caseV2);
				break;
			case referred:
        caseGovernorConnectorV2.createInternalCases(prepareCase(caseV2));
				break;
			default:
				// Nothing to do, only return status
		}
	}

	private CasesListV2 prepareCase(CaseV2 newCase) {
		CasesListV2 outboundCases = new CasesListV2();
		outboundCases.add(newCase);
		return outboundCases;
	}
}


