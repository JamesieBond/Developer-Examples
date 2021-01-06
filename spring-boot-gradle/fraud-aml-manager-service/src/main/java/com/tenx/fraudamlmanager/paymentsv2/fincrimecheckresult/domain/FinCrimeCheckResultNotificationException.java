package com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain;

public class FinCrimeCheckResultNotificationException extends Exception {

  public FinCrimeCheckResultNotificationException(String message) {
    super(message);
  }

  public FinCrimeCheckResultNotificationException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
