package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.cases.domain;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseCreationResult;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseGovernorConnectorV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.CasesListV2;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.TransactionCaseDataStore;
import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultV3;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class FinCrimeCheckResultCaseServiceV3Impl implements FinCrimeCheckResultCaseServiceV3 {

  private static final String CASE_DELETION_FAILED =
      "FinCrime Check Result case deletion failed for transaction ID: {}";
  private static final String CASE_CREATION_FAILED =
      "FinCrime Check Result case creation failed for transaction ID: {}";
  private static final String CASE_CREATION_UNEXPECTED_ERROR =
      "Unexpected error processed for Id: {}";
  private static final String NO_CASE_REFERENCE = "Failed retrieve case reference. transactionId: {}, skipping case creation";
  private static final String CASE_GOVERNOR_REQUEST_ERROR = "Failed request case governor. transactionId: {}";
  private final PaymentCaseDataStore paymentCaseDataStore;
  private final CaseGovernorConnectorV2 caseGovernorConnector;
  private final TransactionCaseDataStore transactionCaseDataStore;

  public void processCaseForFinCrimeCheckResult(FinCrimeCheckResultV3 finCrimeCheckResultV3) {
    String transactionId = finCrimeCheckResultV3.getTransactionId();
    try {
      if (finCrimeCheckResultV3.isReferred() || finCrimeCheckResultV3.hasExternalCases()) {
        paymentCaseDataStore.findCaseByTransactionId(transactionId)
            .ifPresentOrElse(casev2 -> requestCaseGovernor(casev2)
                    .ifPresentOrElse(caseResult -> handleCaseCreationResult(transactionId, caseResult),
                        () -> log.error(CASE_GOVERNOR_REQUEST_ERROR, transactionId)),
                () -> log.warn(NO_CASE_REFERENCE, transactionId));

      } else {
        paymentCaseDataStore.deleteCaseByTransactionId(transactionId);
      }
    } catch (PaymentCaseException e) {
      log.error(CASE_CREATION_FAILED, finCrimeCheckResultV3.getTransactionId(), e);
    } catch (Exception e) {
      log.error(CASE_CREATION_UNEXPECTED_ERROR, finCrimeCheckResultV3.getTransactionId(), e);
    }
  }

  public void cleanupCaseWithFinalOutcome(FinCrimeCheckResultV3 finCrimeCheckResultV3) {
    String transactionId = finCrimeCheckResultV3.getTransactionId();
    try {
      if (finCrimeCheckResultV3.isFinalOutcome()) {
        paymentCaseDataStore.deleteCaseByTransactionId(transactionId);
      }
    } catch (PaymentCaseException e) {
      log.error(CASE_DELETION_FAILED, finCrimeCheckResultV3.getTransactionId(), e);
    }
  }

  private void handleCaseCreationResult(String transactionId, CaseCreationResult caseCreationResult) {
    try {
      transactionCaseDataStore.saveTransactionCase(transactionId, caseCreationResult.getCaseId());
    } catch (PaymentCaseException e) {
      log.error(CASE_CREATION_FAILED, transactionId, e);
    }
  }

  private CasesListV2 prepareCase(CaseV2 newCase) {
    CasesListV2 outboundCases = new CasesListV2();
    outboundCases.add(newCase);
    return outboundCases;
  }

  private Optional<CaseCreationResult> requestCaseGovernor(CaseV2 newCase) {
    CasesListV2 caseListV2 = prepareCase(newCase);
    return Optional.ofNullable(caseGovernorConnector.createInternalCases(caseListV2))
        .flatMap(caseCreationResultList -> caseCreationResultList.stream().findFirst());
  }

}


