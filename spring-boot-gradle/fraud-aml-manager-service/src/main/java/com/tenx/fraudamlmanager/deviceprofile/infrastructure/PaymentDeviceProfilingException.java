package com.tenx.fraudamlmanager.threatmetrix.infrastructure.deviceprofile;


import com.tenx.fraudamlmanager.payments.client.exceptions.ErrorDetails;
import lombok.Getter;

public class PaymentDeviceProfilingException extends Exception {

  @Getter
  private final ErrorDetails errorDetails;

  public PaymentDeviceProfilingException(int errorCode, String message) {
    super(message);
    errorDetails = new ErrorDetails(errorCode, message);
  }

  public PaymentDeviceProfilingException(int errorCode, String message, Throwable t) {
    super(message, t);
    errorDetails = new ErrorDetails(errorCode, message);
  }
}