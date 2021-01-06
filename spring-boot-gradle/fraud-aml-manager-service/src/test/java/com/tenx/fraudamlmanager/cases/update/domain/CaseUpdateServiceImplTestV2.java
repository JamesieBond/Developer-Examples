package com.tenx.fraudamlmanager.cases.update.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseStatus;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CaseUpdateServiceImplTestV2 {

	private CaseUpdateService caseUpdateService;

	//todo: switch to v3 when v2 payment is deprecated
	@MockBean
	private FinCrimeCheckResultServiceV2 finCrimeCheckResultDomainService;

	@Captor
	ArgumentCaptor<FinCrimeCheckResultV2> finCrimeCheckResultV2ArgumentCaptor;

	@BeforeEach
	public void beforeEach() {
		caseUpdateService = new CaseUpdateServiceImpl(finCrimeCheckResultDomainService);
	}

	@ParameterizedTest
	@MethodSource("paymentCaseEvent")
	public void checkFinCrimeCheckCaseResultApplicableProvisionOutcomeAsBlocked(PaymentCaseUpdate paymentCaseUpdate, FinCrimeCheckResultResponseCodeV2 outcome) throws FinCrimeCheckResultException {

		caseUpdateService.checkForUpdateFinCrimeCheck(paymentCaseUpdate);
		verify(finCrimeCheckResultDomainService, times(1))
				.updateFinCrimeCheckFromEvent(finCrimeCheckResultV2ArgumentCaptor.capture());
		FinCrimeCheckResultV2 finCrimeCheckResultV2 = finCrimeCheckResultV2ArgumentCaptor.getValue();
		Assertions.assertEquals("transactionId", finCrimeCheckResultV2.getTransactionId());
		Assertions.assertEquals(outcome, finCrimeCheckResultV2.getStatus());
	}

	private static Stream<Arguments> paymentCaseEvent() {
		Attribute attributeTransactionId = new Attribute(
						"transactionId",
						"transactionId"
		);
		Attribute attributeTransactionProvisionalOutcome = new Attribute(
						"provisionalOutcome",
						"Blocked"
		);
		PaymentCaseUpdate paymentCaseUpdateClosed = new PaymentCaseUpdate(null, CaseStatus.CLOSED.name(),
						"FRAUD_EXCEPTION",
						"partyKey"
		);
		paymentCaseUpdateClosed.getAttributes().add(attributeTransactionId);
		paymentCaseUpdateClosed.getAttributes().add(attributeTransactionProvisionalOutcome);
		paymentCaseUpdateClosed.setOutcome("PASSED");
		PaymentCaseUpdate paymentCaseUpdateNoStatus = new PaymentCaseUpdate(null, "",
						"FRAUD_EXCEPTION",
						"partyKey"
		);
		paymentCaseUpdateNoStatus.getAttributes().add(attributeTransactionId);
		paymentCaseUpdateNoStatus.getAttributes().add(attributeTransactionProvisionalOutcome);
		paymentCaseUpdateNoStatus.setOutcome("PASSED");

		return Stream.of( Arguments.arguments(paymentCaseUpdateClosed, FinCrimeCheckResultResponseCodeV2.PASSED),
						Arguments.arguments(paymentCaseUpdateNoStatus, FinCrimeCheckResultResponseCodeV2.BLOCKED));
	}

	@Test
	public void checkFinCrimeCheckCaseResultClosedWithOutcome() throws FinCrimeCheckResultException {
		PaymentCaseUpdate paymentCaseUpdate = new PaymentCaseUpdate();
		paymentCaseUpdate.setOutcome("PASSED");
		paymentCaseUpdate.setStatus("Closed");
		paymentCaseUpdate.setCaseType("Fraud_Exception");
		Attribute attribute = new Attribute("transactionId", "someTransactionId");
		paymentCaseUpdate.getAttributes().add(attribute);
		caseUpdateService.checkForUpdateFinCrimeCheck(paymentCaseUpdate);
		verify(finCrimeCheckResultDomainService, times(1))
				.updateFinCrimeCheckFromEvent(finCrimeCheckResultV2ArgumentCaptor.capture());
		FinCrimeCheckResultV2 finCrimeCheckResultV2 = finCrimeCheckResultV2ArgumentCaptor.getValue();
		Assertions.assertEquals("someTransactionId", finCrimeCheckResultV2.getTransactionId());
		Assertions.assertEquals(FinCrimeCheckResultResponseCodeV2.PASSED, finCrimeCheckResultV2.getStatus());
	}


	@Test
	public void checkFinCrimeCheckCaseResultNoOutcome() throws FinCrimeCheckResultException {
		PaymentCaseUpdate caseEventDataV2 = mock(PaymentCaseUpdate.class);
		when(caseEventDataV2.extractValueForTransactionId()).thenReturn("transactionId");
		when(caseEventDataV2.getOutcome()).thenReturn("PASSED");
		when(caseEventDataV2.getStatus()).thenReturn("Closed");
		when(caseEventDataV2.getCaseType()).thenReturn("Fraud_Exception");
		caseUpdateService.checkForUpdateFinCrimeCheck(caseEventDataV2);
		verifyZeroInteractions(finCrimeCheckResultDomainService);
	}

	@Test
	public void checkFinCrimeCheckCaseResultNotProcessing() throws FinCrimeCheckResultException {
		PaymentCaseUpdate caseEventDataV2 = mock(PaymentCaseUpdate.class);
		when(caseEventDataV2.extractValueForTransactionId()).thenReturn("transactionId");
		when(caseEventDataV2.getOutcome()).thenReturn("PASSED");
		when(caseEventDataV2.getStatus()).thenReturn("SomeStatus");
		when(caseEventDataV2.getCaseType()).thenReturn("Fraud_Exception");
		caseUpdateService.checkForUpdateFinCrimeCheck(caseEventDataV2);
		verifyZeroInteractions(finCrimeCheckResultDomainService);
	}
}