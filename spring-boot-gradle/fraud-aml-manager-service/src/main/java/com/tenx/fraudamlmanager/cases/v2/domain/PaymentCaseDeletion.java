package com.tenx.fraudamlmanager.cases.v2.domain;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;

public interface PaymentCaseDeletion {
    void deleteCaseByTransactionId(String transactionId) throws PaymentCaseException;
}
