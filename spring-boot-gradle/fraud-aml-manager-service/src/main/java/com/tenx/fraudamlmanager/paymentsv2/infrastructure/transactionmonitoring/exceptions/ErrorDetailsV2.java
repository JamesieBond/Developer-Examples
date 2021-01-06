package com.tenx.fraudamlmanager.paymentsv2.infrastructure.transactionmonitoring.exceptions;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ErrorDetailsV2 {

  private int httpStatusCode;

  private String message;

}