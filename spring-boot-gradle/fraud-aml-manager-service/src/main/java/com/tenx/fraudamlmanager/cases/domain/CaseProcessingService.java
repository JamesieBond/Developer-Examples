package com.tenx.fraudamlmanager.cases.domain;

public interface CaseProcessingService {

  void processCaseForFinCrimeCheckResult(FinCrimeCheckCase finCrimeCheckCase);

  void cleanupCaseWithFinalOutcome(FinCrimeCheckCase finCrimeCheckCase);
}
