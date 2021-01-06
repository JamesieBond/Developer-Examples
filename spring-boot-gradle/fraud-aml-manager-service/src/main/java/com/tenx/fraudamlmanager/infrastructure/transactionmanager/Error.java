package com.tenx.fraudamlmanager.infrastructure.transactionmanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {
  private static final int TRANSACTION_IS_PROCESSED_ERROR_CODE = 2320;

  private String reason;
  private int code;

  @JsonIgnore
  public Boolean is2320Response() {
    return code == TRANSACTION_IS_PROCESSED_ERROR_CODE;
  }
}
