package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;

public interface TransactionManagerConnector {

  void notifyTransactionManager(FinCrimeCheckResultV2 finCrimeCheckResultV2)
      throws TransactionManagerException;
}
