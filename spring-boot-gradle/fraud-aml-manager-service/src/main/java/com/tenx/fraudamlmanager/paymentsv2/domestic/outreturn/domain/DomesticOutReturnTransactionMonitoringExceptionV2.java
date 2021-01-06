package com.tenx.fraudamlmanager.paymentsv2.domestic.outreturn.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomesticOutReturnTransactionMonitoringExceptionV2 extends Exception {

  private Error error = Error.GENERAL_SERVICE_ERROR;

  public DomesticOutReturnTransactionMonitoringExceptionV2(Error error, String message,
      Throwable throwable) {
    super(message, throwable);
    this.error = error;
  }

  public DomesticOutReturnTransactionMonitoringExceptionV2(Error error, String message) {
    super(message);
    this.error = error;
  }

  public enum Error {
    MISSING_REQUIRED_FIELDS,
    GENERAL_SERVICE_ERROR
  }
}
