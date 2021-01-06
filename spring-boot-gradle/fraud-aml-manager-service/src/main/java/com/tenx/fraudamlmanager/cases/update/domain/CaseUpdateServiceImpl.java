package com.tenx.fraudamlmanager.cases.update.domain;

import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultServiceV2;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaseUpdateServiceImpl implements CaseUpdateService {

	private static final String FIN_CRIME_CHECK_RESULT =
					"Received FinCrime Check Result Event transaction ID: {}, for Status: {}";
	private static final String FIN_CRIME_CHECK_SUCCESS =
					"FinCrime Check Result successfully processed for Id: {}";

	//todo: replace FinCrimeCheckResultServiceV2 with FinCrimeCheckResultServiceV3 when deprecating v2
	private final FinCrimeCheckResultServiceV2 finCrimeCheckResultServiceV2;

	private static final String FIN_CRIME_RESULT_ERROR = "Failed Processing Fin Crime Check Result.";

	private static final String FIN_CRIME_RESULT_ERROR_WITH_TRANSACTION_ID =
					"Failed Processing Fin Crime Check Result, transactionId: {}";

	@Value("${DISABLE_CASE_OUTCOME_EVENT}")
	private boolean DISABLE_CASE_OUTCOME_EVENT;


	public void checkForUpdateFinCrimeCheck(PaymentCaseUpdate paymentCaseUpdate) throws FinCrimeCheckResultException {

		log.info(FIN_CRIME_CHECK_RESULT, paymentCaseUpdate.extractValueForTransactionId(), paymentCaseUpdate.getStatus());

		if (paymentCaseUpdate.isFraudException() && (paymentCaseUpdate.isClosedWithOutcome() || paymentCaseUpdate.isApplicableForFinCrimeCheckWithProvisionalOutcomeAsBlocked())) {
			postFraudFinCrimeCheckEventNotification(paymentCaseUpdate);
		}
		log.info(FIN_CRIME_CHECK_SUCCESS, paymentCaseUpdate.extractValueForTransactionId());

	}

	private void postFraudFinCrimeCheckEventNotification(PaymentCaseUpdate paymentCaseUpdate) throws FinCrimeCheckResultException {

		log.info("CaseEventV2 event partyKey {}", paymentCaseUpdate.getPartyKey());

		FinCrimeCheckResultV2 finCrimeCheckResultV2 = new FinCrimeCheckResultV2(paymentCaseUpdate.extractValueForTransactionId(),
						FinCrimeCheckResultResponseCodeV2.valueOf(paymentCaseUpdate.getOutcome().toUpperCase()));

		checkCaseOutEnabled(finCrimeCheckResultV2);
	}

	private void checkCaseOutEnabled(FinCrimeCheckResultV2 finCrimeCheckResult) throws FinCrimeCheckResultException {
		try {
			if (!DISABLE_CASE_OUTCOME_EVENT) {
				finCrimeCheckResultServiceV2.updateFinCrimeCheckFromEvent(finCrimeCheckResult);
			}
		} catch (Exception e) {
			log.error(FIN_CRIME_RESULT_ERROR_WITH_TRANSACTION_ID, finCrimeCheckResult.getTransactionId());
			throw new FinCrimeCheckResultException(
							HttpStatus.INTERNAL_SERVER_ERROR.value(), FIN_CRIME_RESULT_ERROR, e);
		}
	}

}
