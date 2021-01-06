package com.tenx.fraudamlmanager.cases.v2.domain;

interface PaymentCaseCreation {
    void createCase(String transactionId, String paymentType, CaseV2 paymentCase);
}
