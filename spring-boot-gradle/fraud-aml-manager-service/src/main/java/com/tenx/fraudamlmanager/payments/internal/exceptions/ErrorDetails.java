package com.tenx.fraudamlmanager.payments.internal.exceptions;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ErrorDetails {
  private int httpStatusCode;

  private String message;

}
