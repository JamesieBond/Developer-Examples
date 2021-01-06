package com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.cases.domain;

import com.tenx.fraudamlmanager.paymentsv3.fincrimecheckresult.domain.FinCrimeCheckResultV3;

public interface FinCrimeCheckResultCaseServiceV3 {
    void processCaseForFinCrimeCheckResult(FinCrimeCheckResultV3 finCrimeCheckResult);

  void cleanupCaseWithFinalOutcome(FinCrimeCheckResultV3 finCrimeCheckResult);
}
