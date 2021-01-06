package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;

import com.tenx.fraudamlmanager.cases.domain.CaseProcessingService;
import com.tenx.fraudamlmanager.cases.domain.FinCrimeCheckCase;
import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinCrimeCheckResultServiceImplV2 implements FinCrimeCheckResultServiceV2 {

  private static final String FIN_CRIME_CHECK_RESULT =
      "FinCrime Check Result transaction ID: {}, for Status: {}";

  private static final String TRANSACTION_MANAGER_CLIENT_ERROR =
      "Failed to notify Transaction Manager.";

  private static final String FPS_RAILS_FRAUD_CHECK_RESPONSE_ERROR = "Failed to notify FPS Rails.";

  private static final String TRANSACTION_MANAGER_ERROR_WITH_TRANSACTION_ID =
      "Failed to notify Transaction Manager, transactionId: {}";

  private static final String FPS_RAILS_FRAUD_CHECK_RESPONSE_ERROR_WITH_TRANSACTION_ID =
      "Failed to notify FPS Rails, transactionId: {}";

  private final TransactionManagerConnector transactionManagerConnector;

  private final CaseProcessingService caseProcessingService;

  private final DomesticFinCrimeCheckResultNotificationService
      domesticFinCrimeCheckResultNotificationService;

  @Value("${FPS_FINCRIME_CHECK_OUTCOME_TO:TransactionManager}")
  private FinCrimeCheckResultReceiver finCrimeCheckResultReceiver;

  @Override
  public void updateFinCrimeCheck(FinCrimeCheckResultV2 finCrimeCheckResult)
      throws FinCrimeCheckResultException {
    log.info(FIN_CRIME_CHECK_RESULT, finCrimeCheckResult.getTransactionId(), finCrimeCheckResult.getStatus());
    try {
      notifyResult(finCrimeCheckResult);
    } finally {
      FinCrimeCheckCase finCrimeCheckCase = FinCrimeCheckResultToCaseMapperV2.MAPPER
          .toFinCrimeCheckCase(finCrimeCheckResult);
      caseProcessingService.processCaseForFinCrimeCheckResult(finCrimeCheckCase);
    }

  }

  @Override
  public void updateFinCrimeCheckFromEvent(FinCrimeCheckResultV2 finCrimeCheckResult)
      throws FinCrimeCheckResultException {
    log.info(FIN_CRIME_CHECK_RESULT, finCrimeCheckResult.getTransactionId(), finCrimeCheckResult.getStatus());
    try {
      notifyResult(finCrimeCheckResult);
    } finally {
      FinCrimeCheckCase finCrimeCheckCase = FinCrimeCheckResultToCaseMapperV2.MAPPER
          .toFinCrimeCheckCase(finCrimeCheckResult);
      caseProcessingService.cleanupCaseWithFinalOutcome(finCrimeCheckCase);
    }
  }


  public void notifyResult(FinCrimeCheckResultV2 finCrimeCheckResult)
      throws FinCrimeCheckResultException {

    if (FinCrimeCheckResultReceiver.TRANSACTION_MANAGER.equals(finCrimeCheckResultReceiver)) {

      notifyTransactionManager(finCrimeCheckResult);

    } else if (FinCrimeCheckResultReceiver.FPSRAILS.equals(finCrimeCheckResultReceiver)) {

      notifyFPSRails(finCrimeCheckResult);

    }
  }

  private void notifyTransactionManager(FinCrimeCheckResultV2 finCrimeCheckResult)
      throws FinCrimeCheckResultException {
    try {
      transactionManagerConnector.notifyTransactionManager(finCrimeCheckResult);
    } catch (TransactionManagerException e) {
      log.error(
          TRANSACTION_MANAGER_ERROR_WITH_TRANSACTION_ID, finCrimeCheckResult.getTransactionId());
      throw new FinCrimeCheckResultException(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), TRANSACTION_MANAGER_CLIENT_ERROR, e);
    }
  }

  private void notifyFPSRails(FinCrimeCheckResultV2 finCrimeCheckResult)
      throws FinCrimeCheckResultException {
    try {

      domesticFinCrimeCheckResultNotificationService.notifyDomesticFinCrimeCheckResult(
          finCrimeCheckResult);

    } catch (FinCrimeCheckResultNotificationException | PaymentCaseException | TransactionManagerException e) {

      log.error(
          FPS_RAILS_FRAUD_CHECK_RESPONSE_ERROR_WITH_TRANSACTION_ID,
          finCrimeCheckResult.getTransactionId());
      throw new FinCrimeCheckResultException(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), FPS_RAILS_FRAUD_CHECK_RESPONSE_ERROR, e);
    }
  }

  public enum FinCrimeCheckResultReceiver {
    TRANSACTION_MANAGER("TransactionManager"),
    FPSRAILS("FPSRails");

    private String value;

    FinCrimeCheckResultReceiver(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

}
