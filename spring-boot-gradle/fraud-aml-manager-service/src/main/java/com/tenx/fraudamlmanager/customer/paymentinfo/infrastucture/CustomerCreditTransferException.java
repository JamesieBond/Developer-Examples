package com.tenx.fraudamlmanager.customer.paymentinfo.infrastucture;

import lombok.Getter;

@Getter
public class CustomerCreditTransferException extends Exception {

  private int httpStatus;
  private String detailedMessage;

  public CustomerCreditTransferException(int errCode, String message) {
    super(message);
    httpStatus = errCode;
    detailedMessage = message;
  }

  public CustomerCreditTransferException(int errCode, String message, Throwable e) {
    super(message, e);
    httpStatus = errCode;
    detailedMessage = message;
  }
}
