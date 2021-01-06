package com.tenx.fraudamlmanager.onboarding.payee.api;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PayeeData {

  private String name;

  @NotEmpty
  @NotNull
  private String partyKey;

  @NotEmpty
  @NotNull
  private String payeeId;

  @NotEmpty
  @NotNull
  private String accountId;

  @NotEmpty
  @NotNull
  private String authenticationMethod;

  private ChangeType changeType;

  @Valid
  @NotNull
  private PayeeBeneficiary beneficiary;

  private List<PayeeAccount> payeeAccounts;

  public Optional<String> extractBeneficiaryId() {
    return extractPayeeAccountsData()
      .map(PayeeAccount::getIdentification);
  }

  private String extractPayeeName() {
    return extractPayeeAccountsData()
      .map(PayeeAccount::getName)
      .orElse("");
  }


  public String extractBeneficiaryFirstName() {
    String firstName = "";

    if (extractPayeeName() != null) {
      firstName = extractPayeeName().split(" ")[0];
    }

    return firstName;
  }

  public String extractBeneficiaryLastName() {
    String lastName = "";

    if (extractPayeeName() != null) {
      String[] splittedName = extractPayeeName().split(" ");
      lastName = splittedName[splittedName.length - 1];
    }
    return lastName;
  }

  public String extractBeneficiaryFirstNameForPayeesUpdate() {
    String firstName = "";

    if (getName() != null && !getName().isEmpty()) {
      firstName = getName().split(" ")[0];
    }

    return firstName;
  }

  public String extractBeneficiaryLastNameForPayeesUpdate() {
    String lastName = "";

    if (getName() != null && !getName().isEmpty()) {
      String[] splittedName = getName().split(" ");
      lastName = splittedName[splittedName.length - 1];
    }
    return lastName;
  }

  private Optional<PayeeAccount> extractPayeeAccountsData() {
    return payeeAccounts.stream()
      .findFirst();
  }

}
