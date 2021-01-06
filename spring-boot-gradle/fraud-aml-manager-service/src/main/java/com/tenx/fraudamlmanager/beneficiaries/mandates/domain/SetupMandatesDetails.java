package com.tenx.fraudamlmanager.beneficiaries.mandates.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupMandatesDetails {

  @NotEmpty
  @NotNull
  private String partyKey;

  @NotEmpty
  @NotNull
  private String reference;

  @NotEmpty
  @NotNull
  private String accountName;

  @NotEmpty
  @NotNull
  private String directDebitKey;

  @NotNull
  @NotEmpty
  private String action;

  public boolean isActionApplicable() {
    if (action != null) {
      return (action.equals("SETUP") || action.equals("CANCELLATION"));
    } else {
      return false;
    }
  }
}
