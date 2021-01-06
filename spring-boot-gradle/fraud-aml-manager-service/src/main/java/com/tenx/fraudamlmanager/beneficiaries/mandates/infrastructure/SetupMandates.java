package com.tenx.fraudamlmanager.beneficiaries.mandates.infrastructure;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupMandates {

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
  private BeneficiaryAction action;

}
