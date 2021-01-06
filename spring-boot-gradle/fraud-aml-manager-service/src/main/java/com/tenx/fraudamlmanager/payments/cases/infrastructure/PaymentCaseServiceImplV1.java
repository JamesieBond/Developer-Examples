package com.tenx.fraudamlmanager.payments.cases.infrastructure;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.v1.domain.Case;
import com.tenx.fraudamlmanager.cases.v1.domain.CaseAssembler;
import com.tenx.fraudamlmanager.cases.v1.domain.CasesList;
import com.tenx.fraudamlmanager.payments.cases.PaymentCaseServiceV1;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment;
import feign.FeignException;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCaseServiceImplV1 implements PaymentCaseServiceV1 {

	private static final String EXCEPTION_SEND_CASE = "Case Creation Error:";
	private static final String CASE_POSTED_TO_CASE_GOVERNOR = "Case sent to Case Governor. caseId: {}";

	private static final String CASE_CREATION_BLOCKED_FAILED_MSG = "Case creation for blocked payment failed: {}";
	private static final String CASE_CREATED_MSG = "Case created for caseId: {}";

	private final CaseAssembler caseAssembler;
	private final CaseGovernorClient caseGovernorClient;

	private CasesList prepareCase(Case newCase) {
		CasesList outboundCases = new CasesList();
		outboundCases.add(newCase);
		return outboundCases;
	}

	private String getCaseId(List<CaseCreationResponse> response) {

		return response.stream()
						.findFirst()
						.map(CaseCreationResponse::getCaseId)
						.orElse("unknown");
	}

	public void createBlockedPaymentCase(DomesticOutPayment domesticOutPayment) {

		try {
			Case outboundCase = caseAssembler.assembleCase(domesticOutPayment);
			List<CaseCreationResponse> response = postCase(outboundCase);
			log.info(CASE_CREATED_MSG, getCaseId(response));

		} catch (NoSuchElementException | FeignException | PaymentCaseException e) {
			log.error(CASE_CREATION_BLOCKED_FAILED_MSG, e.getMessage(), e);
		}
	}

	public void createBlockedPaymentCase(OnUsPayment onUsPayment) {

		try {
			Case outboundCase = caseAssembler.assembleCase(onUsPayment);
			postCase(outboundCase);
			List<CaseCreationResponse> response = postCase(outboundCase);
			log.info(CASE_CREATED_MSG, getCaseId(response));

		} catch (NoSuchElementException | FeignException | PaymentCaseException e) {
			log.error(CASE_CREATION_BLOCKED_FAILED_MSG, e.getMessage(), e);
		}
	}

	private List<CaseCreationResponse> postCase(Case outboundCase) throws PaymentCaseException {
		try {
			List<CaseCreationResponse> response = caseGovernorClient.createCases(prepareCase(outboundCase));
			log.info(CASE_POSTED_TO_CASE_GOVERNOR, response.get(0).getCaseId());
			return response;
		} catch (FeignException e) {
			throw new PaymentCaseException(EXCEPTION_SEND_CASE, e);
		}
	}

}


