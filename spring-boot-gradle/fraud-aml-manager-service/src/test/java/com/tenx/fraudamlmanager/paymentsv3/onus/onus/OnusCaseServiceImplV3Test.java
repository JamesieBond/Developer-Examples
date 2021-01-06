package com.tenx.fraudamlmanager.paymentsv3.onus.onus;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseGovernorConnectorV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseHandlerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.cases.domain.OnUsAssemblerV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.cases.domain.OnUsCaseServiceImplV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.cases.domain.OnUsCaseServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class OnusCaseServiceImplV3Test {

	private OnUsCaseServiceV3 onUsCaseServiceV3;

	@MockBean
	private CaseHandlerV2 caseHandlerV2;

	@MockBean
	private OnUsAssemblerV3 caseAssembler;

	@MockBean
  private CaseGovernorConnectorV2 caseGovernorConnectorV2;

	@Captor
	ArgumentCaptor<CasesListV2> onUsCaseListCapture;

	@BeforeEach
	public void beforeEach() {
		this.onUsCaseServiceV3 = new OnUsCaseServiceImplV3(caseAssembler, caseHandlerV2);
	}

	@Test
	void savePaymentCaseForPendingCases() {
		OnUsPaymentV3 onusPaymentV3 = mockOnUs();
		CaseV2 caseV2 = new CaseV2();
		caseV2.setCaseType("FRAUD_EXCEPTION");
		when(caseAssembler.assembleCase(any())).thenReturn(caseV2);
		onUsCaseServiceV3.processCaseForOnUs(FraudAMLSanctionsCheckResponseCodeV3.pending, onusPaymentV3);
		verify(caseAssembler, times(1)).assembleCase(eq(onusPaymentV3));
		verify(caseHandlerV2, times(1)).processPaymentCase(eq(FraudAMLSanctionsCheckResponseCodeV3.pending), eq("transactionId"), anyString(), eq(caseV2));
	}

	private OnUsPaymentV3 mockOnUs() {
		OnUsPaymentV3 onusPaymentV3 = mock(OnUsPaymentV3.class);
		when(onusPaymentV3.getDebtorPartyKey()).thenReturn("partyKey");
		when(onusPaymentV3.getCreditorName()).thenReturn("creditor");
		when(onusPaymentV3.getTransactionId()).thenReturn("transactionId");
		return onusPaymentV3;
	}


}