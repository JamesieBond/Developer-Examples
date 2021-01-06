package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;


import com.tenx.fraudamlmanager.payments.fincrimecheckresult.domain.FinCrimeCheckResultException;

public interface FinCrimeCheckResultServiceV2 {

  void updateFinCrimeCheck(FinCrimeCheckResultV2 finCrimeCheckResult) throws FinCrimeCheckResultException;

  void updateFinCrimeCheckFromEvent(FinCrimeCheckResultV2 finCrimeCheckResult)
          throws FinCrimeCheckResultException;
}
