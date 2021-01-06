package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain;

import com.tenx.fraudamlmanager.cases.domain.CaseProcessingService;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinCrimeCheckResultServiceV3Impl implements FinCrimeCheckResultServiceV3 {

  private static final String FIN_CRIME_CHECK_RESULT =
      "FinCrime Check Result transaction ID: {}, for Status: {}";

  private static final String FAILED_POSTING_KAFKA_EVENT =
      "FinCrime Check Result failed producing fraud check event, transaction ID: %s";
  private static final String FIN_CRIME_CHECK_SUCCESS =
      "FinCrime Check Result successfully processed for Id: {}";

  private final CaseProcessingService caseProcessingService;

  @Value("${DISABLE_CASE_OUTCOME_EVENT}")
  private boolean DISABLE_CASE_OUTCOME_EVENT;

  @Override
  public void updateFinCrimeCheck(FinCrimeCheckResultV3 finCrimeCheckResult) {
    FinCrimeCheckCase finCrimeCheckCase = FinCrimeCheckResultToCaseMapperV3.MAPPER
        .toFinCrimeCheckCase(finCrimeCheckResult);
    caseProcessingService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);
  }

  @Override
  public void updateFinCrimeCheckFromEvent(FinCrimeCheckResultV3 finCrimeCheckResult) {
    log.info(FIN_CRIME_CHECK_RESULT, finCrimeCheckResult.getTransactionId(), finCrimeCheckResult.getStatus());
    FinCrimeCheckCase finCrimeCheckCase = FinCrimeCheckResultToCaseMapperV3.MAPPER
        .toFinCrimeCheckCase(finCrimeCheckResult);
    caseProcessingService.cleanupCaseWithFinalOutcome(finCrimeCheckCase);
  }
}
