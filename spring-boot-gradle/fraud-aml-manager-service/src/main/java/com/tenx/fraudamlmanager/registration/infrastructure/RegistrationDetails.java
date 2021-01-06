package com.tenx.fraudamlmanager.registration.infrastructure;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDetails {

  @NotEmpty
  @NotNull
  private String partyKey;

  @NotNull
  @NotEmpty
  private Boolean passcodeProvided;

  @NotNull
  @NotEmpty
  private Boolean passwordProvided;
}
