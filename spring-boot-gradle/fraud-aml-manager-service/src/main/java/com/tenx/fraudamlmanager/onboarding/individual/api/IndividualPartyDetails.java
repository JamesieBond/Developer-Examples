package com.tenx.fraudamlmanager.onboarding.individual.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tenx.fraudamlmanager.application.DateUtils;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Slf4j
public class IndividualPartyDetails {

  @NotNull
  @NotEmpty
  private String partyKey;

  @NotNull
  @NotEmpty
  private String updateType;

  private String mobileNumber;

  private String email;

  private String givenName;

  private String middleName;

  private String lastName;

  @JsonIgnore
  private int birthDate;

  private LocalDate dateOfBirth;

  @JsonIgnore
  private List<Address> addresses;

  private Address currentAddress;

  public void setBirthDate(int birthDate) {
    this.birthDate = birthDate;
    updateDateOfBirth(birthDate);
  }

  private void updateDateOfBirth(int birthDate) {
      this.dateOfBirth = DateUtils.createLocalDateFromIntEpochSecond(birthDate);
  }

  public void setAddress(List<Address> addresses) {
    this.addresses = addresses;
    this.currentAddress = extractCurrentAddress(addresses);
  }

  private Address extractCurrentAddress(List<Address> addresses) {
    return addresses.stream()
            .filter(address -> address.isAddressTypeHome() && address.isStatusActive())
            .findFirst().orElse(null);
  }

}
