package com.tenx.fraudamlmanager.cases.domain.internal;

import com.tenx.fraudamlmanager.cases.domain.CaseGovernorConnector;
import com.tenx.fraudamlmanager.cases.domain.CaseMapper;
import com.tenx.fraudamlmanager.cases.domain.CaseProcessingService;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorException;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseCreationResult;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.TransactionCaseDataStore;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "CASE_CREATION_TYPE", havingValue = "INTERNAL", matchIfMissing = true)
public class InternalCasesService implements CaseProcessingService {

  private static final String CASE_CREATION_FAILED =
      "FinCrime Check Result case creation failed for transaction ID: {}";
  private static final String CASE_RESULT_SAVE =
      "Case result record created for transaction ID: {}";
  private static final String CASE_CREATION_UNEXPECTED_ERROR =
      "Unexpected error processed for Id: {}";

  private static final String NO_CASE_REFERENCE = "Failed retrieve case reference. transactionId: {}";
  private static final String CASE_DELETION_FAILED =
      "FinCrime Check Result case deletion failed for transaction ID: {}";
  private static final String CASE_GOVERNOR_REQUEST_ERROR = "Failed request case governor. transactionId: {}";

  private final TransactionCaseDataStore transactionCaseDataStore;
  private final PaymentCaseDataStore paymentCaseDataStore;
  private final CaseGovernorConnector caseGovernorConnector;

  public void processCaseForFinCrimeCheckResult(FinCrimeCheckCase finCrimeCheckCase) {
    if (finCrimeCheckCase.isReferred() || !finCrimeCheckCase.getExternalCases().isEmpty()) {
      createNewInternalCase(finCrimeCheckCase);
    } else {
      deletePaymentCase(finCrimeCheckCase.getTransactionId());
    }
  }

  public void cleanupCaseWithFinalOutcome(FinCrimeCheckCase finCrimeCheckCase) {
    if (finCrimeCheckCase.isFinalOutcome()) {
      deletePaymentCase(finCrimeCheckCase.getTransactionId());
    }
  }

  public void createNewInternalCase(FinCrimeCheckCase finCrimeCheckCase) {
    String transactionId = finCrimeCheckCase.getTransactionId();
    try {
      paymentCaseDataStore.findCaseByTransactionId(transactionId).ifPresent(caseV2 -> {
        sendCaseToCaseGovernor(CaseMapper.MAPPER.toInternalCase(caseV2), transactionId);
      });
    } catch (PaymentCaseException e) {
      log.error(NO_CASE_REFERENCE, transactionId);
    }
  }

  private void sendCaseToCaseGovernor(InternalCase internalCase, String transactionId) {
    try {
      List<CaseCreationResult> caseCreationResults = requestCaseGovernor(internalCase);
      handleCaseCreationResult(transactionId, caseCreationResults);
    } catch (CaseGovernorException ex) {
      log.error(CASE_GOVERNOR_REQUEST_ERROR, ex.getMessage());
    }
  }

  private void handleCaseCreationResult(String transactionId, List<CaseCreationResult> caseCreationResult) {
    try {
      if (!caseCreationResult.isEmpty()) {
        log.info(CASE_RESULT_SAVE, transactionId);
        transactionCaseDataStore.saveTransactionCase(transactionId, getCaseId(caseCreationResult));
      }
    } catch (PaymentCaseException e) {
      log.error(CASE_CREATION_FAILED, transactionId);
    }
  }

  private InternalCases prepareInternalCase(InternalCase newCase) {
    InternalCases outboundCases = new InternalCases();
    outboundCases.add(newCase);
    return outboundCases;
  }

  private List<CaseCreationResult> requestCaseGovernor(InternalCase newCase) throws CaseGovernorException {
    InternalCases caseListV2 = prepareInternalCase(newCase);
    return caseGovernorConnector.createInternalCases(caseListV2);
  }

  private void deletePaymentCase(String transactionId) {
    try {
      paymentCaseDataStore.deleteCaseByTransactionId(transactionId);
    } catch (PaymentCaseException e) {
      log.error(CASE_DELETION_FAILED, transactionId);
    }
  }

  private String getCaseId(List<CaseCreationResult> response) {
    return response.stream()
        .findFirst()
        .map(CaseCreationResult::getCaseId)
        .orElse("unknown");
  }
}

