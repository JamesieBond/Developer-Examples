package com.tenx.fraudamlmanager.payments.internal.exceptions;

public class PaymentBlockedException extends Exception {

  ErrorDetails errorDetails;

  public PaymentBlockedException(int errCode, String message){
    super(message);
    errorDetails = new ErrorDetails(errCode, message);
  }

  public PaymentBlockedException(int errCode, String message, Throwable e){
    super(message, e);
    errorDetails = new ErrorDetails(errCode, message);
  }

}
