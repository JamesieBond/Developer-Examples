package com.tenx.fraudamlmanager.cases.infrastructure.governor;

import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.ErrorDetails;

public class CaseGovernorException extends Exception {

  private ErrorDetails errorDetails;

  public CaseGovernorException(int errCode, String message) {
    super(message);
    errorDetails = new ErrorDetails(errCode, message);
  }

  public CaseGovernorException(int errCode, String message, Throwable e) {
    super(message, e);
    errorDetails = new ErrorDetails(errCode, message);
  }
}