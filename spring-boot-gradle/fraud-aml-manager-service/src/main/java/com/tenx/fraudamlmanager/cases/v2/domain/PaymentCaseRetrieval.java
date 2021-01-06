package com.tenx.fraudamlmanager.cases.v2.domain;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;
import java.util.Optional;

interface PaymentCaseRetrieval {

    Optional<CaseV2> findCaseByTransactionId(String transactionId) throws PaymentCaseException;

    Optional<String> findPaymentTypeByTransactionId(String transactionId)
        throws PaymentCaseException;
}
