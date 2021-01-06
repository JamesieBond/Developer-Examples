package com.tenx.fraudamlmanager.onboarding.individual.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tenxbanking.party.event.PartyContactStatusV3;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class Address {

    public static final String ADDRESS_TYPE_HOME = "HOME";

    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private String addressLine4;

    private String addressLine5;

    private String postCode;

    private String city;

    private String country;

    private String state;

    private String addressType;

    private String status;

    @JsonIgnore
    public boolean isAddressTypeHome() {
        return ADDRESS_TYPE_HOME.equals(addressType);
    }

    @JsonIgnore
    public boolean isStatusActive() {
        return PartyContactStatusV3.ACTIVE.equals(PartyContactStatusV3.valueOf(status));
    }

}
