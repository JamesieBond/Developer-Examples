package com.tenx.fraudamlmanager.onboarding.business.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tenxbanking.party.event.business.AddressType;
import com.tenxbanking.party.event.business.BusinessAddressStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class Address {

    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private String addressLine4;

    private String addressLine5;

    private String postCode;

    private String city;

    private String country;

    private String state;

    private String status;

    private String addressType;

    @JsonIgnore
    public boolean isAddressTypeREGD() {
        return AddressType.REGD.equals(AddressType.valueOf(addressType));
    }

    @JsonIgnore
    public boolean isActive() {
        return BusinessAddressStatus.ACTIVE.equals(BusinessAddressStatus.valueOf(status));
    }
}
