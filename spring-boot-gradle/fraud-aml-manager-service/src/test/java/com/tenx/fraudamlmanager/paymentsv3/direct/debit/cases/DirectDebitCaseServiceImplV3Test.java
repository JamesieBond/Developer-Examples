package com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseHandlerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases.domain.DirectDebitCaseAssemblerV3;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases.domain.DirectDebitCaseServiceImplV3;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.cases.domain.DirectDebitCaseServiceV3;
import com.tenx.fraudamlmanager.paymentsv3.direct.debit.domain.DirectDebitPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domain.FraudAMLSanctionsCheckResponseCodeV3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DirectDebitCaseServiceImplV3Test {

	private DirectDebitCaseServiceV3 directDebitCaseServiceV3;
	@MockBean
	private CaseHandlerV2 caseHandlerV2;

	@MockBean
	private PaymentCaseDataStore caseDataStore;

	@MockBean
	private DirectDebitCaseAssemblerV3 caseAssembler;


	@Captor
	ArgumentCaptor<DirectDebitPaymentV3> directDebitPaymentForCaseV3ArgumentCaptor;

	@Captor
	private ArgumentCaptor<CasesListV2> casesListV2ArgumentCaptor;

	@BeforeEach
	public void beforeEach() {
		this.directDebitCaseServiceV3 = new DirectDebitCaseServiceImplV3(caseHandlerV2, caseAssembler);
	}

	@Test
	void processPaymentCase() {
		DirectDebitPaymentV3 directDebitPaymentV3 = mockDirectDebit();
		CaseV2 caseV2 = new CaseV2();
		caseV2.setCaseType("FRAUD_EXCEPTION");
		when(caseAssembler.assembleCase(any())).thenReturn(caseV2);
		directDebitCaseServiceV3.processCaseForDirectDebit(FraudAMLSanctionsCheckResponseCodeV3.pending, directDebitPaymentV3);
		verify(caseAssembler, times(1)).assembleCase(directDebitPaymentForCaseV3ArgumentCaptor.capture());
		verify(caseHandlerV2, times(1)).processPaymentCase(eq(FraudAMLSanctionsCheckResponseCodeV3.pending), eq("transactionId"), anyString(), eq(caseV2));
		verifyDirectDebitForCase(directDebitPaymentForCaseV3ArgumentCaptor.getValue());
	}

	private void verifyDirectDebitForCase(DirectDebitPaymentV3 directDebitPaymentV3) {
		assertEquals("transactionId", directDebitPaymentV3.getTransactionId());
		assertEquals("creditor", directDebitPaymentV3.getCreditorName());
		assertEquals("partyKey", directDebitPaymentV3.getPartyKey());
	}

	private DirectDebitPaymentV3 mockDirectDebit() {
		DirectDebitPaymentV3 directDebitPaymentV3 = mock(DirectDebitPaymentV3.class);
		when(directDebitPaymentV3.getPartyKey()).thenReturn("partyKey");
		when(directDebitPaymentV3.getCreditorName()).thenReturn("creditor");
		when(directDebitPaymentV3.getTransactionId()).thenReturn("transactionId");
		return directDebitPaymentV3;
	}


}