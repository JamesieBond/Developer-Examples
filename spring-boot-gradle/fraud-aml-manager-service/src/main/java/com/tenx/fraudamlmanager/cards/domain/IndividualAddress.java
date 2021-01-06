package com.tenx.fraudamlmanager.cards.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualAddress {

  public enum Status {
    ACTIVE,
    PENDING_VERIFICATION,
    EXPIRED
  }

  private String postCode;
  private Status status;

  public boolean isStatusActive() {
    return Status.ACTIVE.equals(status);
  }

}
