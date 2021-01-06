package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;

import com.tenx.fraudamlmanager.paymentsv2.infrastructure.transactionmonitoring.exceptions.ErrorDetailsV2;
import lombok.Getter;

public class CaseEventException extends Exception {

	@Getter
	private ErrorDetailsV2 errorDetails;

	public CaseEventException(int errCode, String message) {
		super(message);
		errorDetails = new ErrorDetailsV2(errCode, message);
	}

	public CaseEventException(int errCode, String message, Throwable e) {
		super(message, e);
		errorDetails = new ErrorDetailsV2(errCode, message);
	}
}
