package com.tenx.fraudamlmanager.paymentsv2.api;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ErrorDetails {

  private int httpStatusCode;

  private String message;

}