package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain;

import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;

public interface FinCrimeCheckResultServiceV3 {

	void updateFinCrimeCheck(FinCrimeCheckResultV3 finCrimeCheckResult) throws FinCrimeCheckResultException;

	void updateFinCrimeCheckFromEvent(FinCrimeCheckResultV3 finCrimeCheckResult)
					throws FinCrimeCheckResultException;
}
