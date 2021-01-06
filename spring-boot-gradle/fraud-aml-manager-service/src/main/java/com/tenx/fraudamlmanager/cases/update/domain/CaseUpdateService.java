package com.tenx.fraudamlmanager.cases.update.domain;

import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;

public interface CaseUpdateService {

  void checkForUpdateFinCrimeCheck(PaymentCaseUpdate caseEventData) throws FinCrimeCheckResultException;
}
