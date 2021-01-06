package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;


import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;

public interface DomesticFinCrimeCheckResultNotificationService {

  void notifyDomesticFinCrimeCheckResult(FinCrimeCheckResultV2 finCrimeCheckResult)
      throws FinCrimeCheckResultNotificationException, PaymentCaseException, TransactionManagerException;
}
