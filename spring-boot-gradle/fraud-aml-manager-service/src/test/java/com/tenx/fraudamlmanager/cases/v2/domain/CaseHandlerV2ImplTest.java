package com.tenx.fraudamlmanager.cases.v2.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CaseHandlerV2ImplTest {

	private CaseHandlerV2 caseHandlerV2;
	@MockBean
  private CaseGovernorConnectorV2 caseGovernorConnectorV2;

	@MockBean
	private PaymentCaseDataStore caseDataStore;


	@Captor
	private ArgumentCaptor<CasesListV2> casesListV2ArgumentCaptor;

	@BeforeEach
	public void beforeEach() {
    this.caseHandlerV2 = new CaseHandlerV2Impl(caseDataStore, caseGovernorConnectorV2);
	}

	@Test
	void savePaymentCaseForPendingCases() {
		CaseV2 caseV2 = new CaseV2();
		caseV2.setCaseType("FRAUD_EXCEPTION");
		caseHandlerV2.processPaymentCase(FraudAMLSanctionsCheckResponseCodeV3.pending, "transactionId", "someType", caseV2);
		verify(caseDataStore, times(1)).createCase(eq("transactionId"), eq("someType"), eq(caseV2));

	}

	@Test
	void createReferredPaymentCase() {
		CaseV2 caseV2 = new CaseV2();
		caseV2.setCaseType("FRAUD_EXCEPTION");
		caseHandlerV2.processPaymentCase(FraudAMLSanctionsCheckResponseCodeV3.referred, "transactionId", "someType", caseV2);
    verify(caseGovernorConnectorV2, times(1)).createInternalCases(casesListV2ArgumentCaptor.capture());
		assertEquals("FRAUD_EXCEPTION", casesListV2ArgumentCaptor.getValue().getCases().get(0).caseType);
	}

	@Test
	void createDomesticInOtherPaymentCase() {
		CaseV2 caseV2 = new CaseV2();
		caseV2.setCaseType("FRAUD_EXCEPTION");
		caseHandlerV2.processPaymentCase(FraudAMLSanctionsCheckResponseCodeV3.passed, "transactionId", "someType", caseV2);
		verifyZeroInteractions(caseDataStore);
    verifyZeroInteractions(caseGovernorConnectorV2);
	}

}