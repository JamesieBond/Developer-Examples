package com.tenx.fraudamlmanager.payments.core.credittransfer.infrastructure;

public class CreditTransferPublishException extends Exception {

  public CreditTransferPublishException(String message) {
    super(message);
  }

  public CreditTransferPublishException(String message, Throwable e) {
    super(message, e);
  }

}
