package com.tenx.fraudamlmanager.paymentsv2.domestic.in.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomesticInTransactionMonitoringExceptionV2 extends Exception {

  public enum Error {
    MISSING_REQUIRED_FIELDS,
    GENERAL_SERVICE_ERROR
  }

  private Error error = Error.GENERAL_SERVICE_ERROR;

  public DomesticInTransactionMonitoringExceptionV2(Error error, String message,
                                                    Throwable throwable) {
    super(message, throwable);
    this.error = error;
  }

  public DomesticInTransactionMonitoringExceptionV2(Error error, String message) {
    super(message);
    this.error = error;
  }
}
