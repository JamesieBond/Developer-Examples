package com.tenx.fraudamlmanager.payments.core.credittransfer.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreditTransferTransactionException extends Exception{

  private Error error = Error.GENERAL_SERVICE_ERROR;

  public CreditTransferTransactionException(Error error, String message,
      Throwable throwable) {
    super(message, throwable);
    this.error = error;
  }

  public CreditTransferTransactionException(Error error, String message) {
    super(message);
    this.error = error;
  }

  public CreditTransferTransactionException() {
  }

  public enum Error {
    MISSING_REQUIRED_FIELDS,
    GENERAL_SERVICE_ERROR
  }
}
