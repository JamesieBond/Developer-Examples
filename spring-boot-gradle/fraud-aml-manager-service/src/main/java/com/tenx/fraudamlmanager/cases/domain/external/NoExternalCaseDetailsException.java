package com.tenx.fraudamlmanager.cases.domain.external;

public class NoExternalCaseDetailsException extends Exception {

  public NoExternalCaseDetailsException(String transactionId) {
    super("No external case details found to create Case for ID :" + transactionId);
  }

}
