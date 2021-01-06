package com.tenx.fraudamlmanager.cases.domain.external;

import com.tenx.fraudamlmanager.cases.domain.CaseDetails;
import com.tenx.fraudamlmanager.cases.domain.CaseGovernorConnector;
import com.tenx.fraudamlmanager.cases.domain.CaseMapper;
import com.tenx.fraudamlmanager.cases.domain.CaseProcessingService;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.cases.infrastructure.governor.CaseGovernorException;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import com.tenx.fraudamlmanager.cases.v2.domain.PaymentCaseDataStore;
import com.tenx.fraudamlmanager.cases.v2.infrastructure.TransactionCaseDataStore;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "CASE_CREATION_TYPE", havingValue = "EXTERNAL")
public class ExternalCasesService implements CaseProcessingService {

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
  private static final String CASE_GOVERNOR_UPDATE_ERROR = "Failed to update external case. transactionId: {}";
  private static final String EXTERNAL_CASE_MAPPING_ERROR = "Failed to map external case. transactionId: {}";
  private static final String CASE_TYPE = "FRAUD_EXCEPTION_EXTERNAL";

  private final TransactionCaseDataStore transactionCaseDataStore;
  private final PaymentCaseDataStore paymentCaseDataStore;
  private final CaseGovernorConnector caseGovernorConnector;


  public void processCaseForFinCrimeCheckResult(FinCrimeCheckCase finCrimeCheckCase) {
    if (finCrimeCheckCase.isReferred()) {
      createNewExternalCase(finCrimeCheckCase);
    } else if (finCrimeCheckCase.isUpdate() && finCrimeCheckCase.isExternalCaseCreated()) {
      updateExternalCase(finCrimeCheckCase);
    }
  }

  public void cleanupCaseWithFinalOutcome(FinCrimeCheckCase finCrimeCheckCase) {
    if (finCrimeCheckCase.isFinalOutcome() || finCrimeCheckCase.isBlocked()) {
      deletePaymentCase(finCrimeCheckCase.getTransactionId());
    }
  }

  private void createNewExternalCase(FinCrimeCheckCase finCrimeCheckCase) {
    String transactionId = finCrimeCheckCase.getTransactionId();
    try {
      ExternalCase externalCase = findAndUpdateExternalCase(
          finCrimeCheckCase.getExternalCases().stream().findFirst().orElseThrow(() ->
              new NoExternalCaseDetailsException(transactionId)),
          transactionId, finCrimeCheckCase.getStatus().name());
      sendExternalCaseToCaseGovernor(externalCase, transactionId);
    } catch (PaymentCaseException | NoExternalCaseDetailsException e) {
      log.error(NO_CASE_REFERENCE, transactionId);
    }
  }

  private void updateExternalCase(FinCrimeCheckCase finCrimeCheckCase) {
    String transactionId = finCrimeCheckCase.getTransactionId();

    try {
      ExternalCase externalCase = buildExternalCaseForUpdate(finCrimeCheckCase);
      sendExternalCaseUpdateToCaseGovernor(externalCase);
      deletePaymentCase(transactionId);
    } catch (PaymentCaseException e) {
      log.error(CASE_GOVERNOR_UPDATE_ERROR, transactionId, e);
    }
  }

  private void sendExternalCaseUpdateToCaseGovernor(ExternalCase externalCase)
      throws PaymentCaseException {
    try {
      caseGovernorConnector.updateExternalCase(externalCase.getBpmSystemCaseId(), externalCase);
    } catch (CaseGovernorException e) {
      throw new PaymentCaseException(CASE_GOVERNOR_REQUEST_ERROR, e);
    }
  }

  private ExternalCase buildExternalCaseForUpdate(FinCrimeCheckCase finCrimeCheckCase)
      throws PaymentCaseException {
    String transactionId = finCrimeCheckCase.getTransactionId();
    String caseId = transactionCaseDataStore.findCaseIdByTransactionId(transactionId);
    Optional<CaseV2> caseV2Optional = paymentCaseDataStore.findCaseByTransactionId(transactionId);
    try {
      return caseV2Optional.map(
          caseV2 -> CaseMapper.MAPPER.toExternalCaseForUpdate(caseV2, finCrimeCheckCase, caseId))
          .orElseThrow(() -> new PaymentCaseException(NO_CASE_REFERENCE));
    } catch (IllegalArgumentException e) {
      throw new PaymentCaseException(EXTERNAL_CASE_MAPPING_ERROR, e);
    }
  }

  private ExternalCase findAndUpdateExternalCase(CaseDetails externalCaseDetails, String transactionId,
      String status) throws PaymentCaseException {
    Optional<CaseV2> caseV2Optional = paymentCaseDataStore.findCaseByTransactionId(transactionId);
    if (caseV2Optional.isPresent()) {
      return createExternalCase(caseV2Optional.get(), externalCaseDetails, status);
    } else {
      throw new PaymentCaseException(NO_CASE_REFERENCE);
    }

  }

  private ExternalCase createExternalCase(CaseV2 caseV2, CaseDetails externalCaseDetails, String status) {
    ExternalCase externalCase = CaseMapper.MAPPER.toExternalCase(caseV2);
    externalCase.setBpmSystem(externalCaseDetails.getSourceSystem());
    externalCase.setBpmSystemCaseId(externalCaseDetails.getSourceCaseID());
    externalCase.setCaseType(CASE_TYPE);
    externalCase.setStatus(status);
    externalCase.setDisplayToCustomer(externalCaseDetails.getNotifyCustomer());
    return externalCase;
  }

  private void sendExternalCaseToCaseGovernor(ExternalCase externalCase, String transactionId) {
    try {
      ExternalCaseCreationResult externalCaseCreationResult = requestCaseGovernorExternal(
          externalCase);
      handleCaseCreationResults(transactionId, externalCaseCreationResult);
    } catch (CaseGovernorException ex) {
      log.error(CASE_GOVERNOR_REQUEST_ERROR, ex.getMessage());
    }
  }

  private void handleCaseCreationResults(String transactionId,
      ExternalCaseCreationResult externalCaseCreationResult) {
    try {
      if (externalCaseCreationResult != null) {
        log.info(CASE_RESULT_SAVE, transactionId);
        transactionCaseDataStore
            .saveTransactionCase(transactionId, externalCaseCreationResult.getBpmSystemCaseId());
      }
    } catch (PaymentCaseException e) {
      log.error(CASE_GOVERNOR_REQUEST_ERROR, transactionId);
    }
  }

  private ExternalCaseCreationResult requestCaseGovernorExternal(ExternalCase externalCase)
      throws CaseGovernorException {
    return caseGovernorConnector.createExternalCase(externalCase);
  }

  private void deletePaymentCase(String transactionId) {
    try {
      paymentCaseDataStore.deleteCaseByTransactionId(transactionId);
    } catch (PaymentCaseException e) {
      log.error(CASE_DELETION_FAILED, transactionId);
    }
  }


}

