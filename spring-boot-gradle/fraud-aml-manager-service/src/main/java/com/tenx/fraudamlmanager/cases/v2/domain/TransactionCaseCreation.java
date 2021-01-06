package com.tenx.fraudamlmanager.cases.v2.domain;

import com.tenx.fraudamlmanager.cases.domain.PaymentCaseException;

public interface TransactionCaseCreation {
	//this class is to support the current payment case service
	//todo: delete it eventually
	void createCase(String transactionId, String caseId) throws PaymentCaseException;
}
