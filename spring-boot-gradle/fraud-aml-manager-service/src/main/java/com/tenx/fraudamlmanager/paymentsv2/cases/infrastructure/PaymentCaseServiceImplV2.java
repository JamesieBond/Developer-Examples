package com.tenx.fraudamlmanager.paymentsv2.cases.infrastructure;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.infrastructure.CaseCreationResponse;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseAssemblerV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.CaseListMapperV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.CasesListV2Request;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.PaymentCaseEntityV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.PaymentCaseRepositoryV2;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.TransactionCaseDataStore;
import com.tenx.fraudamlmanager.payments.cases.infrastructure.CaseGovernorClient;
import com.tenx.fraudamlmanager.paymentsv2.cases.domain.PaymentCaseServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain.DomesticInPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.out.domain.DomesticOutPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain.DomesticOutReturnPaymentV2;
import com.tenx.fraudamlmanager.paymentsv2.onus.domain.OnUsPaymentV2;
import feign.FeignException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCaseServiceImplV2 implements PaymentCaseServiceV2 {
	//todo: refactor case creation v2 and delete this class

	private static final String PAYMENT_ENTITY_SAVED = "Payment Entity (including Case) saved on DB, transactionId: {}";
	private static final String CASE_CREATION_FAILED_MSG = "Case creation for payment failed:";
	private static final String EXCEPTION_SEND_CASE = "Case Creation Error:";
	private static final String EXCEPTION_DELETE_CASE = "Case Deletion Error, transactionId : {}";
	private static final String CASE_CREATION_FAILED_IN_DB_MSG = "Storing case creation for payment failed, transactionId : {}";
	private static final String CASE_POSTED_TO_CASE_GOVERNOR = "Case sent to Case Governor. caseId: {}";
	private static final String NO_CASE_REFERENCE = "Failed retrieve case reference. transactionId: {}, skipping case creation";

	private static final String CASE_CREATION_BLOCKED_FAILED_MSG = "Case creation for blocked payment failed: {}";
	private static final String CASE_CREATED_MSG = "Case created for caseId: {}";

	private final PaymentCaseRepositoryV2 paymentCaseRepository;
	private final TransactionCaseDataStore transactionCaseService;
	private final CaseAssemblerV2 caseAssembler;
	private final CaseGovernorClient caseGovernorClient;

	public void createSavePaymentCase(OnUsPaymentV2 onUsPaymentV2) {

		try {
			CaseV2 onUsPaymentCase = caseAssembler.assembleCase(onUsPaymentV2);
			savePaymentEvent(onUsPaymentV2.getTransactionId(), onUsPaymentV2.getClass().getSimpleName(),
							onUsPaymentCase);

		} catch (Exception e) {
			log.error(CASE_CREATION_FAILED_MSG, e);
		}
	}

	public void createSavePaymentCase(DomesticOutPaymentV2 domesticOutPaymentV2) {

		try {
			CaseV2 domesticOutPaymentCase = caseAssembler.assembleCase(domesticOutPaymentV2);
			savePaymentEvent(domesticOutPaymentV2.getTransactionId(), domesticOutPaymentV2.getClass().getSimpleName(),
							domesticOutPaymentCase);

		} catch (Exception e) {
			log.error(CASE_CREATION_FAILED_MSG, e);
		}
	}

	@Override
	public void createSavePaymentCase(DomesticOutReturnPaymentV2 domesticOutReturnPaymentV2) {
		try {
			CaseV2 caseV2 = caseAssembler.assembleCase(domesticOutReturnPaymentV2);
			savePaymentEvent(domesticOutReturnPaymentV2.getTransactionId(), domesticOutReturnPaymentV2.getClass().getSimpleName(),
							caseV2);

		} catch (Exception e) {
			log.error(CASE_CREATION_FAILED_MSG, e);
		}
	}

	@Override
	public void createSavePaymentCase(DomesticInPaymentV2 domesticInPaymentV2) {
		try {
			CaseV2 caseV2 = caseAssembler.assembleCase(domesticInPaymentV2);
			savePaymentEvent(domesticInPaymentV2.getTransactionId(), domesticInPaymentV2.getClass().getSimpleName(),
							caseV2);

		} catch (Exception e) {
			log.error(CASE_CREATION_FAILED_MSG, e);
		}
	}

	public void savePaymentEvent(String transactionId, String paymentType, CaseV2 paymentCase) {
		PaymentCaseEntityV2 paymentCaseEntity = new PaymentCaseEntityV2();
		paymentCaseEntity.setTransactionId(transactionId);
		paymentCaseEntity.setPaymentType(paymentType);
		paymentCaseEntity.setPaymentCase(paymentCase);
		paymentCaseRepository.save(paymentCaseEntity);

		log.info(PAYMENT_ENTITY_SAVED, transactionId);
	}

	private void sendSaveAndDeleteCase(String transactionId, PaymentCaseEntityV2 paymentCaseEntityV2)
					throws PaymentCaseException {
		try {
			CaseV2 newCase = retrieveCase(paymentCaseEntityV2);
			CasesListV2 list = prepareCase(newCase);
			CaseCreationResponse caseCreationResponse = postCase(CaseListMapperV2.MAPPER.toCaseGovernorRequest(list));
			transactionCaseService.saveTransactionCase(transactionId, caseCreationResponse.getCaseId());
			deleteCase(paymentCaseEntityV2);

		} catch (Exception e) {
			throw new PaymentCaseException(EXCEPTION_SEND_CASE, e);
		}
	}

	public void createCase(String transactionId, String caseId) throws PaymentCaseException {
		try {
			log.info("Creating case for transaction Id: {}", transactionId);
			PaymentCaseEntityV2 paymentCaseEntity = paymentCaseRepository.findByTransactionId(transactionId);
			if (Optional.ofNullable(paymentCaseEntity).isPresent()) {
				sendSaveAndDeleteCase(transactionId, paymentCaseEntity);
			} else {
				log.warn(NO_CASE_REFERENCE, transactionId);
			}
		} catch (HibernateException e) {
			log.error(CASE_CREATION_FAILED_IN_DB_MSG, transactionId, e);
		} catch (PaymentCaseException e) {
			throw e;
		} catch (Exception e) {
			throw new PaymentCaseException(CASE_CREATION_FAILED_MSG, e);
		}
	}

	public void deleteCaseByTransactionId(String transactionId) throws PaymentCaseException {
		try {
			PaymentCaseEntityV2 paymentCaseEntity = paymentCaseRepository.findByTransactionId(transactionId);
			deleteCase(paymentCaseEntity);
		} catch (HibernateException e) {
			log.error(CASE_CREATION_FAILED_IN_DB_MSG, transactionId, e);
		} catch (Exception e) {
			throw new PaymentCaseException(EXCEPTION_DELETE_CASE, e);
		}
	}

	public PaymentCaseEntityV2 retrievePaymentCaseEntityV2(String transactionId) {
		return paymentCaseRepository.findByTransactionId(transactionId);
	}

	private CaseV2 retrieveCase(PaymentCaseEntityV2 paymentCaseEntity) {
		return paymentCaseEntity.getPaymentCase();
	}

	private CasesListV2 prepareCase(CaseV2 newCase) {
		CasesListV2 outboundCases = new CasesListV2();
		outboundCases.add(newCase);
		return outboundCases;
	}

	private CaseCreationResponse postCase(CasesListV2Request caselist) throws PaymentCaseException {
		try {
			List<CaseCreationResponse> responses = caseGovernorClient.createCasesV2(caselist);
			log.info(CASE_POSTED_TO_CASE_GOVERNOR, responses.get(0).getCaseId());
			return responses.get(0);
		} catch (FeignException e) {
			throw new PaymentCaseException(EXCEPTION_SEND_CASE, e);
		}
	}

	private void deleteCase(PaymentCaseEntityV2 paymentCaseEntity) {
		if (Optional.ofNullable(paymentCaseEntity).isPresent()) {
			paymentCaseRepository.delete(paymentCaseEntity);
		}
	}


	public void createBlockedPaymentCase(CaseV2 caseV2) {
		try {
			List<CaseCreationResponse> response = caseGovernorClient.createCasesV2(CaseListMapperV2.MAPPER.toCaseGovernorRequest(prepareCase(caseV2)));
			log.info(CASE_CREATED_MSG, getCaseId(response));

		} catch (NoSuchElementException | FeignException e) {
			log.error(CASE_CREATION_BLOCKED_FAILED_MSG, e.getMessage(), e);
		}
	}

	private String getCaseId(List<CaseCreationResponse> response) {

		return response.stream()
						.findFirst()
						.map(CaseCreationResponse::getCaseId)
						.orElse("unknown");
	}

}


