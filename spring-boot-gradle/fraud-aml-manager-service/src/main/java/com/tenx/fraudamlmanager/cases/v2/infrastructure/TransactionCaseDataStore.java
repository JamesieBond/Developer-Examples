package com.tenx.fraudamlmanager.cases.v2.infrastructure;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;

public interface TransactionCaseDataStore {

  String findCaseIdByTransactionId(String transactionId) throws PaymentCaseException;

    void saveTransactionCase(String transactionId, String caseId) throws PaymentCaseException;
}
